import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortIOException;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class Test {

    public static void main(String[] args) throws IOException, InterruptedException{

       /* System.out.println("\nHello World\n");

        System.out.println(new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new java.util.Date()) + ".xlsx");
        System.out.println(System.getProperty("user.dir") + "\\newFolder\\newFile.txt");

        */

        testSerial();
    }

    private static SerialPort findSerialPort() throws InterruptedException {
        boolean foundPort = false;
        SerialPort commPort = null;
        SerialPort[] portList = SerialPort.getCommPorts(); // Should be OS agnostic
        PrintWriter portOut;
        Scanner portIn;

        System.out.println("Searching for Arduino serial port . . .\n");

        // This is for testing purposes.
        for (int i = 0; i < portList.length; i++) {  // Search for port
            try {
                commPort = portList[i];
                commPort.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY); // Arduino parameters
                commPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // dunno
            }
            catch (SerialPortInvalidPortException ignored) {
                continue;   // Check next port
            }

            if (commPort.openPort()) {  // Checks if port is correct
                portOut = new PrintWriter(commPort.getOutputStream(), true);
                portIn = new Scanner(commPort.getInputStream());

                while(!commPort.isOpen()) {
                    // Waits for commPort to fully open
                }
                Thread.sleep(2000); // Wait for Arduino to open its port

                portOut.println("?SerialTest");
                Thread.sleep(500);  // Wait 1000 ms for response.

                if (portIn.hasNext()) {
                    if (portIn.nextLine().equals("Connected")) {
                        foundPort = true;
                        break;
                    }
                    else {
                        commPort.closePort();
                        continue;
                    }
                }
                else {
                    commPort.closePort();
                    continue;
                }
            }
        }

        if (!foundPort) {
            return null;
        }

        return commPort;
    }

    public static void testSerial() throws IOException, InterruptedException {

        SerialPort commPort;

        try {
            commPort = findSerialPort(); // Receives serial port for communication
            if (commPort == null) {
                throw new SerialPortInvalidPortException();
            }

        }
        catch (SerialPortInvalidPortException e) {

            System.out.println("Unable to find Arduino serial port. Closing program."); // Instead have the program repeatedly try again.
            return;

        }

        if (commPort.isOpen()) {                            // If port could be accessed
            System.out.println("Port is open :)");
            System.out.println("Connected to port: " + commPort.getSystemPortName() + "\n");
        }
        else {                                               // Otherwise . . .
            System.out.println("Port isn't open :<");
            return;
        }

        for (Integer i = 0; i < 5; ++i) {
            Thread.sleep(1000);
            commPort.getOutputStream().write(i.byteValue());  // Returns byte value of i
            commPort.getOutputStream().flush(); // Writes bytes from buffer
            System.out.println("Sent number: " + i);
        }
        System.out.println();

        Scanner portIn = new Scanner(new BufferedInputStream(commPort.getInputStream()));

        WriteToExcel workbook = new WriteToExcel();
        workbook.createSheet("test1");
        workbook.createSheet("test2");
        workbook.addRow(1, new Object[]{1, "Hello", "World"});
        workbook.addRow(2, new Object[]{2,"1:38.378", ""});

        Thread.sleep(1000);
        int row = 3;
        while (row < 10) {
            Long time;
            String string;
            string = portIn.nextLine();
            time = Long.parseLong(string.replace("> laserN: ", ""));

            workbook.addRow(row, new Object[]{row, Long.toString(time), ""});

            System.out.println("Row " + row + " added successfully!");
            ++row;
        }
        System.out.println();

        workbook.close();
        System.out.println("Workbook closed");
        System.out.println();


        commPort.addDataListener(new SerialPortDataListener() {
            private String messageBuffer = "";

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                messageBuffer += new String(event.getReceivedData());
                if (messageBuffer.contains("\n")) {
                    String tempString = messageBuffer.substring(0, messageBuffer.indexOf('\n') + 1);
                    this.messageBuffer = messageBuffer.replace(tempString, "");
                    System.out.println(tempString);
                }
                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                if (portIn.hasNextLine()) {
                    System.out.println("Received: " + portIn.nextLine());
                }
            }
        });

        Scanner sysIn = new Scanner(System.in);

        int userInput = 0;
        while (userInput == 0) {
            if (sysIn.hasNextInt()) {
                userInput = sysIn.nextInt();
            }
        }

        if (commPort.closePort()) {
            System.out.println("Port is closed :)");
        } else {
            System.out.println("Port isn't closed :<");
        }
    }

}