package iRobot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;


public class SerialTest implements SerialPortEventListener, Environment {
	SerialPort serialPort;
	double frontIR, leftIR, rightIR;
  double orientation;
  int leftTacho, rightTacho;
  PrintWriter pw;
  
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
                        "/dev/ttyACM0", // Raspberry Pi
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
	};
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	
	BufferedWriter bw;

	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	public void initialize() {
                // the next line is for Raspberry Pi and 
                // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
                System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();
			// pw = new PrintWriter(output);
			
			bw = new BufferedWriter(new OutputStreamWriter(output));

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
			  
			  String line;
	      while ((line = input.readLine()) != null) {
	        char c = line.charAt(0);
	        switch (c) {

	          case 'l' :

	            leftIR = Double.parseDouble(line.substring(1));

	            break;

	          case 'r' :
	            rightIR = Double.parseDouble(line.substring(1));
	            break;

	          case 'f' :
	            frontIR = Double.parseDouble(line.substring(1));
	            break;

	          case 'i' :
	            orientation = Double.parseDouble(line.substring(1));
	            break;

	          case 't' :
	            if (line.substring(1, 2).equalsIgnoreCase("l")) {
	              leftTacho = Integer.parseInt(line.substring(2));
	            } else {
	              rightTacho = Integer.parseInt(line.substring(2));
	            }

	            break;
	        }

	      }	
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	
	
	
	}

	public static void main(String[] args) throws Exception {
		SerialTest main = new SerialTest();
		main.initialize();
		Thread t=new Thread() {
			public void run() {
				//the following line will keep this app alive for 1000 seconds,
				//waiting for events to occur and responding to them (printing incoming messages to console).
				try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
			}
		};
		t.start();
		Thread.sleep(3000);
		System.out.println("Started");
//		main.bw.write("100 100 1");
//		main.bw.flush();
		
		main.setMotors(100,100);
		
		Thread.sleep(3000);
		System.out.println("Round 2");
		
//		main.bw.write("-100 -100 1");
//		main.bw.flush();
		  main.setMotors(-100, -100);
		
		Thread.sleep(3000);
		System.out.println("Round 2");
		
//		main.bw.write("0 0 1");
//		main.bw.flush();
		  
		  main.setMotors(0, 0);
		
		  System.out.println("Reads -");
		  System.out.println("Left IR "+main.readLeftIR());
      System.out.println("Right IR "+main.readRightIR());
      System.out.println("Front IR "+main.readFrontIR());

		  
	}


  @Override
  public double readLeftIR() {

    // TODO Auto-generated method stub
    return leftIR;
  }

  @Override
  public double readRightIR() {

    // TODO Auto-generated method stub
    return rightIR;
  }

  @Override
  public double readFrontIR() {

    // TODO Auto-generated method stub
    return frontIR;
  }

  @Override
  public int readIMU() {

    // TODO Auto-generated method stub
    return (int) orientation;
  }

  @Override
  public int readLeftTacho() {

    // TODO Auto-generated method stub
    return leftTacho;
  }

  @Override
  public int readRightTacho() {

    // TODO Auto-generated method stub
    return rightTacho;
  }
  @Override
  public void setMotors(int left, int right) {

   // pw.println(left + " " + right + " " + 1);
    try {
      bw.write(""+left+" "+right+" "+1);
      bw.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
   
    
  }

  @Override
  public void resetTachoCounts() {

    // TODO Auto-generated method stub
    
  }
}
