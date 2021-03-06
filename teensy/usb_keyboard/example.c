/* Keyboard example for Teensy USB Development Board
 * http://www.pjrc.com/teensy/usb_keyboard.html
 * Copyright (c) 2008 PJRC.COM, LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

#include <avr/io.h>
#include <avr/pgmspace.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include "usb_keyboard.h"

#define LED_CONFIG	(DDRD |= (1<<6))
#define LED_ON		(PORTD &= ~(1<<6))
#define LED_OFF		(PORTD |= (1<<6))
#define CPU_PRESCALE(n)	(CLKPR = 0x80, CLKPR = (n))

uint8_t number_keys[10]=
	{KEY_0,KEY_1,KEY_2,KEY_3,KEY_4,KEY_5,KEY_6,KEY_7,KEY_8,KEY_9};

//uint16_t idle_count=0;
int const lockout_time = 31000;
int lockout_counter = 0;
int main(void)
{
	// uint8_t b, d, mask, i, reset_idle;
	// uint8_t b_prev=0xFF, d_prev=0xFF;

	// set for 16 MHz clock
	CPU_PRESCALE(0);

	// Configure all port B and port D pins as inputs with pullup resistors.
	// See the "Using I/O Pins" page for details.
	// http://www.pjrc.com/teensy/pins.html
	// DDRD = 0x00;
	// DDRB = 0x00;
	// PORTB = 0xFF;
	// PORTD = 0xFF;

	// Initialize the USB, and then wait for the host to set configuration.
	// If the Teensy is powered without a PC connected to the USB port,
	// this will wait forever.
	usb_init();
	while (!usb_configured()) /* wait */ ;

	// Wait an extra second for the PC's operating system to load drivers
	// and do whatever it does to actually be ready for input
	_delay_ms(1000);

	// Configure timer 0 to generate a timer overflow interrupt every
	// 256*1024 clock cycles, or approx 61 Hz when using 16 MHz clock
	// This demonstrates how to use interrupts to implement a simple
	// inactivity timeout.
	// TCCR0A = 0x00;
	// TCCR0B = 0x05;
	// TIMSK0 = (1<<TOIE0);

	//First send enter to turn on screen
	usb_keyboard_press(KEY_ENTER, 0);
	_delay_ms(2000);

	for (int i = 0; i <= 9; i++)
	{
		for (int j = 0; j <= 9; j++)
		{
			for (int k = 0; k <= 9; k++)
			{
				for (int l = 0; l <= 9; l++)
				{
					if (i == 0) {usb_keyboard_press(KEY_0, 0);}
					else if (i == 1){usb_keyboard_press(KEY_1, 0);}
					else if (i == 2){usb_keyboard_press(KEY_2, 0);}
					else if (i == 3){usb_keyboard_press(KEY_3, 0);}
					else if (i == 4){usb_keyboard_press(KEY_4, 0);}
					else if (i == 5){usb_keyboard_press(KEY_5, 0);}
					else if (i == 6){usb_keyboard_press(KEY_6, 0);}
					else if (i == 7){usb_keyboard_press(KEY_7, 0);}
					else if (i == 8){usb_keyboard_press(KEY_8, 0);}
					else if (i == 9){usb_keyboard_press(KEY_9, 0);}
					//_delay_ms(50);

					if (j == 0) {usb_keyboard_press(KEY_0, 0);}
					else if (j == 1){usb_keyboard_press(KEY_1, 0);}
					else if (j == 2){usb_keyboard_press(KEY_2, 0);}
					else if (j == 3){usb_keyboard_press(KEY_3, 0);}
					else if (j == 4){usb_keyboard_press(KEY_4, 0);}
					else if (j == 5){usb_keyboard_press(KEY_5, 0);}
					else if (j == 6){usb_keyboard_press(KEY_6, 0);}
					else if (j == 7){usb_keyboard_press(KEY_7, 0);}
					else if (j == 8){usb_keyboard_press(KEY_8, 0);}
					else if (j == 9){usb_keyboard_press(KEY_9, 0);}
					//_delay_ms(50);

					if (k == 0) {usb_keyboard_press(KEY_0, 0);}
					else if (k == 1){usb_keyboard_press(KEY_1, 0);}
					else if (k == 2){usb_keyboard_press(KEY_2, 0);}
					else if (k == 3){usb_keyboard_press(KEY_3, 0);}
					else if (k == 4){usb_keyboard_press(KEY_4, 0);}
					else if (k == 5){usb_keyboard_press(KEY_5, 0);}
					else if (k == 6){usb_keyboard_press(KEY_6, 0);}
					else if (k == 7){usb_keyboard_press(KEY_7, 0);}
					else if (k == 8){usb_keyboard_press(KEY_8, 0);}
					else if (k == 9){usb_keyboard_press(KEY_9, 0);}
					//_delay_ms(50);

					if (l == 0) {usb_keyboard_press(KEY_0, 0);}
					else if (l == 1){usb_keyboard_press(KEY_1, 0);}
					else if (l == 2){usb_keyboard_press(KEY_2, 0);}
					else if (l == 3){usb_keyboard_press(KEY_3, 0);}
					else if (l == 4){usb_keyboard_press(KEY_4, 0);}
					else if (l == 5){usb_keyboard_press(KEY_5, 0);}
					else if (l == 6){usb_keyboard_press(KEY_6, 0);}
					else if (l == 7){usb_keyboard_press(KEY_7, 0);}
					else if (l == 8){usb_keyboard_press(KEY_8, 0);}
					else if (l == 9){usb_keyboard_press(KEY_9, 0);}
					//_delay_ms(50);

					usb_keyboard_press(KEY_ENTER, 0);

					lockout_counter++;

					_delay_ms(50);

					if (lockout_counter == 5){
						lockout_counter = 0;
						_delay_ms(lockout_time / 6);
						usb_keyboard_press(KEY_ENTER, 0);
						_delay_ms(lockout_time / 6);
						usb_keyboard_press(KEY_ENTER, 0);
						_delay_ms(lockout_time / 6);
						usb_keyboard_press(KEY_ENTER, 0);
						_delay_ms(lockout_time / 6);
						usb_keyboard_press(KEY_ENTER, 0);
						_delay_ms(lockout_time / 6);
						usb_keyboard_press(KEY_ENTER, 0);
						_delay_ms(lockout_time / 6);
					}
					/*string number = i.ToString() + j.ToString() + k.ToString() + l.ToString();
					ListBox1.Items.Add(yournumber);*/
				}
			}
		}
	}
}

// 	while (1) {
// 		// read all port B and port D pins
// 		b = PINB;
// 		d = PIND;
// 		// check if any pins are low, but were high previously
// 		mask = 1;
// 		reset_idle = 0;
// 		for (i=0; i<8; i++) {
// 			if (((b & mask) == 0) && (b_prev & mask) != 0) {
// 				usb_keyboard_press(KEY_B, KEY_SHIFT);
// 				usb_keyboard_press(number_keys[i], 0);
// 				reset_idle = 1;
// 			}
// 			if (((d & mask) == 0) && (d_prev & mask) != 0) {
// 				usb_keyboard_press(KEY_D, KEY_SHIFT);
// 				usb_keyboard_press(number_keys[i], 0);
// 				reset_idle = 1;
// 			}
// 			mask = mask << 1;
// 		}
// 		// if any keypresses were detected, reset the idle counter
// 		if (reset_idle) {
// 			// variables shared with interrupt routines must be
// 			// accessed carefully so the interrupt routine doesn't
// 			// try to use the variable in the middle of our access
// 			cli();
// 			idle_count = 0;
// 			sei();
// 		}
// 		// now the current pins will be the previous, and
// 		// wait a short delay so we're not highly sensitive
// 		// to mechanical "bounce".
// 		b_prev = b;
// 		d_prev = d;
// 		_delay_ms(2);
// 	}
// }

// This interrupt routine is run approx 61 times per second.
// A very simple inactivity timeout is implemented, where we
// will send a space character.
// ISR(TIMER0_OVF_vect)
// {
// 	idle_count++;
// 	if (idle_count > 61 * 8) {
// 		idle_count = 0;
// 		usb_keyboard_press(KEY_SPACE, 0);
// 	}
// }


