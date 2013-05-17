import kivy
kivy.require('1.6.0')
import subprocess
import sys
from kivy.app import App
from kivy.uix.button import Button
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.floatlayout import FloatLayout
from kivy.uix.popup import Popup
from kivy.uix.label import Label
from kivy.uix.textinput import TextInput
from kivy.uix.accordion import Accordion, AccordionItem
from kivy.uix.image import Image
from kivy.uix.widget import Widget
#from kivy.uix.camera import Camera
#from kivy.core.camera import Camera as CoreCamera
#from kivy.core.image import Image as CoreImage
#from kivy.uix.anchorlayout import AnchorLayout
from kivy.properties import NumericProperty, ListProperty, BooleanProperty

IsAndroid = sys.platform == 'linux3'  

if IsAndroid:
	from jnius import autoclass, cast

	PythonActivity = autoclass('org.renpy.android.PythonActivity')
	Intent = autoclass('android.content.Intent')
	Uri = autoclass('android.net.Uri')
	jFile = autoclass('java.io.File')
	jEnvironment = autoclass('android.os.Environment')
	jMediaStore = autoclass('android.provider.MediaStore')

class SploitApp(App):

	def popup(self):
		wut = 0
		return

	def popup_pin(self, value):
		#Define layout for buttons
		buttons = BoxLayout(orientation='horizontal')
		btncmp = Button(text='Compare', size_hint_y=None, height='50sp')
		btnclose = Button(text='Cancel', size_hint_y=None, height='50sp')
		buttons.add_widget(btncmp)
		buttons.add_widget(btnclose)

		#Define layout for text inputs
		content = BoxLayout(orientation='vertical')
		file1 = TextInput(multiline=False)
		file2 = TextInput(multiline=False)
		content.add_widget(Label(text='File 1:'))
		content.add_widget(file1)
		content.add_widget(Label(text='File 2:'))
		content.add_widget(file2)
		content.add_widget(buttons)
		popup = Popup(content=content, title='Please enter file paths',
						size_hint=(None, None), size=('300dp', '300dp'))

		#Bind buttons
		btncmp.bind(on_release=popup.dismiss)
		btnclose.bind(on_release=popup.dismiss)
		popup.open()
		return

	def popup_adb(self, value):
		#Define layout for buttons
		buttons = BoxLayout(orientation='horizontal')
		btnclose = Button(text='Cancel', size_hint_y=None, height='50sp')
		buttons.add_widget(btnclose)

		#Define layout for text inputs
		content = BoxLayout(orientation='vertical')
		btnmanip = Button(text='Manipulate lock file', size_hint_y=None, height='50sp')
		btngethash = Button(text='Crack hashes from lock file', size_hint_y=None, height='50sp')

		self.popupadb = Popup(content=content, title='Choose your exploiting method',
						size_hint=(None, None), size=('300dp', '300dp'))
		content.add_widget(btnmanip)
		content.add_widget(btngethash)
		content.add_widget(buttons)

		#Bind buttons
		btnclose.bind(on_release=self.popupadb.dismiss)
		btnmanip.bind(on_release=self.popup_adb_manip)
		btngethash.bind(on_release=self.popupadb.dismiss)
		self.popupadb.open()
		return

	def popup_adb_manip(self, value):
		#First, kill the previous popup
		self.popupadb.dismiss()

		#Define layout for buttons
		buttons = BoxLayout(orientation='horizontal')
		btnclose = Button(text='Cancel', size_hint_y=None, height='50sp')
		buttons.add_widget(btnclose)

		#Define layout for text inputs
		content = BoxLayout(orientation='vertical')
		btnbackup = Button(text='Backup lock file', size_hint_y=None, height='50sp')
		btnrestore = Button(text='Restore lock file', size_hint_y=None, height='50sp')
		btndelete = Button(text='Delete lock file', size_hint_y=None, height='50sp')

		self.popupadbmanip = Popup(content=content, title='Choose your exploiting method',
						size_hint=(None, None), size=('300dp', '300dp'))
		content.add_widget(btnbackup)
		content.add_widget(btnrestore)
		content.add_widget(btndelete)
		content.add_widget(buttons)

		#Bind buttons
		btnclose.bind(on_release=self.popupadbmanip.dismiss)
		btnbackup.bind(on_release=self.popupadbmanip.dismiss)
		btnrestore.bind(on_release=self.popupadbmanip.dismiss)
		btndelete.bind(on_release=self.popupadbmanip.dismiss)
		self.popupadbmanip.open()
		return

	def popup_testadb(self, value):
		#Define layout for buttons
		buttons = BoxLayout(orientation='horizontal')
		btnclose = Button(text='Close', size_hint_y=None, height='50sp')
		buttons.add_widget(btnclose)

		#Define layout for text inputs
		content = BoxLayout(orientation='vertical')
		loladb = subprocess.check_output(['adb', 'devices'])
		adboutput = TextInput(text=str(loladb))
		content.add_widget(adboutput)
		content.add_widget(buttons)
		popup = Popup(content=content, title='Please enter file paths',
						size_hint=(None, None), size=('300dp', '300dp'))

		#Bind buttons
		btnclose.bind(on_release=popup.dismiss)
		popup.open()
		return

	# def testcam(self, value):
	# 	path = './'
	# 	path += time.strftime("%B-%_e-%_I-%M-")
	# 	path += '.png'
	# 	android.cameraCapturePicture(path, True)

	def doscreenshot(self,*largs):
		Window.screenshot(name='screenshot%(counter)04d.jpg')

	def testcam(self, *largs):
		#cam = Camera(resolution=(800, 600),play=True)
		if IsAndroid:
			#photo = jFile(jEnvironment.getExternalStorageDirectory(), "pic.jpg")
			intent = Intent('android.media.action.IMAGE_CAPTURE')
			#intent.putExtra(jMediaStore.EXTRA_OUTPUT, Uri.parse("file:///sdcard/pic.jpg"))
			currentActivity = cast('android.app.Activity', PythonActivity.mActivity)
			currentActivity.startActivity(intent)
		return

	def on_pause(self, *largs):
		return True

	def on_resume(self, *largs):
		return

	# def testcam(self,*largs):
	# 	camwidget = Widget()  #Create a camera Widget
	# 	cam = Camera()        #Get the camera
	# 	cam=Camera(resolution=(640,480), size=(500,500))
	# 	cam.play=True         #Start the camera
	# 	camwidget.add_widget(cam)
	# 	#################################
	# 	# I HAVE NO IDEA WHAT I'M DOING #
	# 	#################################
	# 	#img = Image(self.texture)
	# 	#img.save('pic.png')
	# 	button=Button(text='screenshot',size_hint=(0.12,0.12))
	# 	button.bind(on_press=self.doscreenshot)
	# 	camwidget.add_widget(button)    #Add button to Camera Widget
	# 	self.root.add_widget(camwidget)
	# 	return

	def build(self):
		acc = Accordion()
		
		##This is where we setup the two pages
		page1 = AccordionItem(title='Bruteforce')
		page2 = AccordionItem(title='Exploit')
		page3 = AccordionItem(title='Test')
		
		##Page 1
		one = FloatLayout()
		onelabel = Label(text='Choose what type of lock to bruteforce', font_size='20sp', pos_hint={'x':0.0, 'y':0.3})
		pinbut =  Button(text=str('PIN Code'), size_hint=(0.3, 0.3), pos_hint={'x':0.1, 'y':0.4})
		patbut = Button(text=str('Pattern'), size_hint=(0.3, 0.3), pos_hint={'x':0.6, 'y':0.4})
		one.add_widget(onelabel)
		one.add_widget(pinbut)
		one.add_widget(patbut)
		page1.add_widget(one)
		
		##Page 2
		two = FloatLayout()
		twolabel = Label(text='Choose what type of exploit to perform', font_size='20sp', pos_hint={'x':0.0, 'y':0.3})
		adbbut = Button(text=str('ADB'), size_hint=(0.3, 0.3), pos_hint={'x':0.35, 'y':0.4})
		two.add_widget(twolabel)
		two.add_widget(adbbut)
		page2.add_widget(two)

		##Page 3 -- ADB test
		three = FloatLayout()
		adbtestbut = Button(text=str('Test ADB'), size_hint=(0.3, 0.3), pos_hint={'x':0.1, 'y':0.4})
		camtestbut = Button(text=str('Test Cam'), size_hint=(0.3, 0.3), pos_hint={'x':0.6, 'y':0.4})
		three.add_widget(adbtestbut)
		three.add_widget(camtestbut)
		page3.add_widget(three)
		
		##Add both pages to accordion
		acc.add_widget(page3)
		acc.add_widget(page2)
		acc.add_widget(page1)

		##This is where we bind the buttons to the functions
		pinbut.bind(on_release=self.popup_pin)
		patbut.bind(on_release=self.popup())
		adbbut.bind(on_release=self.popup_adb)
		adbtestbut.bind(on_release=self.popup_testadb)
		camtestbut.bind(on_release=self.testcam)
		
		##Make all the widgets magically appear
		self.root = FloatLayout(orientation='vertical', spacing=20, padding=20)
		self.root.add_widget(acc)
		
		##Set background, index set so it's at the back
		self.background = Image(source='./images/background.jpg')
		self.root.add_widget(self.background, index=len(self.root.children))
		
		return self.root

if __name__ == '__main__':
    SploitApp().run()