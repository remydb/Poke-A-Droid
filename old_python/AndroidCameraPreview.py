import kivy
kivy.require('1.6.0')

from kivy.app import App
from kivy.clock import Clock
from kivy.uix.image import Image
from kivy.properties import BooleanProperty, ListProperty, NumericProperty, StringProperty
from kivy.graphics import RenderContext, Callback, BindTexture
from kivy.graphics.texture import Texture
from kivy.core.window import Window
from kivy.logger import Logger
import sys

GL_TEXTURE_EXTERNAL_OES = 36197

IsAndroid = sys.platform == 'linux3'  

if IsAndroid:
    from jnius import autoclass, cast

    # MediaRecorder Java classes

    Camera = autoclass('android.hardware.Camera')

    Surface = autoclass('android.view.Surface')

    SurfaceTexture = autoclass('android.graphics.SurfaceTexture')

    MediaRecorder = autoclass('android.media.MediaRecorder')
    AudioSource = autoclass('android.media.MediaRecorder$AudioSource')
    VideoSource = autoclass('android.media.MediaRecorder$VideoSource')
    OutputFormat = autoclass('android.media.MediaRecorder$OutputFormat')
    AudioEncoder = autoclass('android.media.MediaRecorder$AudioEncoder')
    VideoEncoder = autoclass('android.media.MediaRecorder$VideoEncoder')
    CamcorderProfile = autoclass('android.media.CamcorderProfile')

class AndroidCameraPreview(Image):
    """
    Android camera preview widget. Extends Image with a custom shader that
    displays textures of target GL_TEXTURE_EXTERNAL_OES. Note, this requires
    Android API 11 because of it's use of SurfaceTexture.
    """
    play = BooleanProperty(False)
    resolution = ListProperty([720, 720])
    camera_id = NumericProperty(-1)


    _camera = None
    _previewCallback = None
    _previewTexture = None

    _previewSurface = None
    _secondary_texture = None


    def __init__(self, **kwargs):
        self.canvas = RenderContext()

        super(AndroidCameraPreview, self).__init__(**kwargs)
        self.bind(resolution=self._resolution_changed)
        self.bind(camera_id=self._camera_id_changed)
        self.bind(play=self._play_changed)


        # This is needed for the default vertex shader.
        self.canvas['projection_mat'] = Window.render_context['projection_mat']


        with self.canvas:  
            Callback(self._draw_callback)
            BindTexture(texture=self._secondary_texture, index=1)
        self.canvas['secondary_texture'] = 1
        self._init_texture(self.resolution)

    def start(self, play=True):
        self._init_camera(self.camera_id)
        self.play = play

    def stop(self):
        self._release_camera()

    def _init_texture(self, resolution):
        width, height = resolution
        # Image looks at self.texture to determine layout, so we create
        # texture but don't actually display it.
        self.texture = Texture.create(size=(height, width))
        self.texture_size = self.texture.size
        if IsAndroid:
            self._secondary_texture = Texture(width=height, height=width, target=GL_TEXTURE_EXTERNAL_OES, colorfmt='rgba')
            self._secondary_texture.bind()
            self._previewTexture = SurfaceTexture(int(self._secondary_texture.id))

    def _init_camera(self, camera_id):
        Logger.info('Init camera %d' % camera_id)
        if self._camera and self.camera_id == camera_id:
            return
        self._release_camera()
        if IsAndroid:
            self._camera = Camera.open(camera_id)
            parameters = self._camera.getParameters()            

            #print parameters.flatten()
            if parameters is None:
                Logger.warning('Can''t read parameters')
                return

            supportedSizes = parameters.getSupportedVideoSizes()
            if supportedSizes is None:
                supportedSizes = parameters.getSupportedPreviewSizes()
           
            sizes = []
            if supportedSizes is not None:
                  iterator = supportedSizes.iterator()
                  while iterator.hasNext():
                      cameraSize = iterator.next()
                      sizes.append((cameraSize.width, cameraSize.height))

            pickedSize = self._pick_optimal_size(sizes)

            # Update texture according to picked size
            self.resolution = list(pickedSize)

            parameters.setPreviewSize(*pickedSize)
            self._camera.setParameters(parameters)
            self._camera.setPreviewTexture(self._previewTexture)

    def get_camera(self):
        return self._camera

    def _resolution_changed(self, instance, value):
        Logger.info('Resolution changed to ' + str(value))
        self._init_texture(value)

    def _camera_id_changed(self, instance, value):
        Logger.info('Changed camera %d' % value)
        if not IsAndroid:
            return

        # Transform orientation based on selected camera
        if self.camera_id == 0:
            sampleVec = ('1.0-tex_coord0.y','1.0-tex_coord0.x')
        elif self.camera_id == 1:
            sampleVec = ('tex_coord0.y','1.0-tex_coord0.x')
        else:
            raise Exception('Invalid camera id')

        self.canvas.shader.fs = '''

            #extension GL_OES_EGL_image_external : require
            #ifdef GL_ES
                precision highp float;
            #endif

            /* Outputs from the vertex shader */
            varying vec4 frag_color;
            varying vec2 tex_coord0;

            /* uniform texture samplers */
            uniform sampler2D texture0;
            uniform samplerExternalOES texture1;

            void main()
            {
                // Flip & Mirror coordinates to rotate source texture by 90 degress
                gl_FragColor = texture2D(texture1, vec2(%s,%s));
            }
        ''' % (sampleVec[0], sampleVec[1])

    def _play_changed(self, instance, value):
        Logger.info('camera %d _play_changed %d' % (self.camera_id, value))
        if not IsAndroid:
            return

        if value:
            if not self._camera:
                self._init_camera(self.camera_id)        
            self._camera.startPreview()
            Clock.schedule_interval(self._update_canvas, 1.0/30)
        else:
            if self._camera:
                Clock.unschedule(self._update_canvas)
                self._camera.stopPreview()

    def _release_camera(self):
        self.play = False
        if self._camera:
            self._camera.release()
            self._camera = None

    def _pick_optimal_size(self, sizes):        
        # Now just returns the one guaranteed support size
        return sizes[0]

    def _draw_callback(self, instr):
        if self._previewTexture:
            self._previewTexture.updateTexImage()

    def _update_canvas(self, dt):
        self.canvas.ask_update()
