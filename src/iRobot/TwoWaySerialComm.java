
package iRobot;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import javax.comm.*;
import java.util.*;

public class TwoWaySerialComm implements Environment {

	PrintWriter pw;
	BufferedReader br;
	double frontIR, leftIR, rightIR;
	double orientation;
	int leftTacho, rightTacho;

	void connect(String portName) throws Exception {

		String wantedPortName = portName;

		Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
		//
		// Check each port identifier if
		// (a) it indicates a serial (not a parallel) port, and
		// (b) matches the desired name.
		//
		CommPortIdentifier portIdentifier = null; // will be set if port found
		System.out.println("Starting search.");
		while (portIdentifiers.hasMoreElements()) {
			CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers
					.nextElement();
			System.out.println("Found: " + pid.getPortType() + " with name: " + pid.getName());

			if (pid.getPortType() == CommPortIdentifier.PORT_SERIAL
					&& pid.getName().equals(wantedPortName)) {
				portIdentifier = pid;
				break;
			}
		}
		if (portIdentifier == null) {
			System.err.println("Could not find serial port " + wantedPortName);
			System.exit(1);
		}

		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(),
					2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();

				PrintWriter pw = new PrintWriter(out);
				br = new BufferedReader(new InputStreamReader(in));

			} else {
				System.out.println(
						"Error: Only serial ports are handled by this example.");
			}
		}
	}

	void read() {

		try {
			String line;
			while ((line = br.readLine()) != null) {
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
			e.printStackTrace();
		}
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

		pw.println(left + " " + right + " " + 1);

		// TODO Auto-generated method stub

	}

	@Override
	public void resetTachoCounts() {

		// TODO Auto-generated method stub

	}
}
