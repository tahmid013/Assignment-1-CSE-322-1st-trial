package Threading;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        boolean logged_in = false;


        Socket socket = new Socket("localhost", 6666);
        System.out.println("Connection established");
        System.out.println("Remote port: " + socket.getPort());
        System.out.println("Local port: " + socket.getLocalPort());

        // buffers
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Scanner myObj = new Scanner(System.in);
        //
        while(true) {

            if(!logged_in) {

                System.out.println("Enter Student id: (" + logged_in+") :");
                String Student_id = myObj.next();
                String add = String.valueOf(socket.getLocalPort());

                out.writeObject(add);
                out.writeObject(Student_id); // send student id
                //String msg = (String) in.readObject();
                //System.out.println(msg);
            }
            if((boolean)in.readObject())
                logged_in =true;
            else
                logged_in = false;

            if(logged_in) {
                System.out.println("Press 1 to look up list of at least once to server");
                System.out.println("Press 2 to look up own folder");
                System.out.println("Press 3 to look up other folder");
                System.out.println("Press 4 to request a  file");
                System.out.println("Press 5 to view unread msg");
                System.out.println("Press 6 to upload a file");
                System.out.println("Press 7 to Exit connection");

                int choice = myObj.nextInt();
                out.writeObject((int) choice);

                if (choice == 6) {


                    byte[] file_len = new byte[50000]; // max size
                    try {
                        System.out.println("Press 1 to upload as public");
                        System.out.println("Press 2 to upload as private");

                        int p_c = myObj.nextInt();
                        out.writeObject((int) p_c);
                        String folder = "public";
                        if (p_c == 2) {
                            folder = "private";
                        }
                        String file_name = "capture.jpg";

                        out.writeObject(file_name);
                        File file = new File(file_name);
                        FileInputStream fis = new FileInputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        OutputStream os = socket.getOutputStream();
                        byte[] contents;
                        long fileLength = file.length();
                        out.writeObject(String.valueOf(fileLength));        //These two lines are used
                        out.flush();                                    //to send the file size in bytes.

                        long current = 0;

                        while (current != fileLength) {
                            int size = 10000;
                            if (fileLength - current >= size)
                                current += size;
                            else {
                                size = (int) (fileLength - current);
                                current = fileLength;
                            }
                            contents = new byte[size];
                            bis.read(contents, 0, size);
                            os.write(contents);
                            //System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
                        }
                        //os.flush();
                        System.out.println("File sent successfully!");
                    } catch (Exception e) {
                        System.err.println("Could not transfer file." + e);
                    }
                    out.flush();
                } else if (choice == 7) {
                    System.out.println("Closing connection from server");
                    logged_in = false;
                    socket.close();
                }
            }
        }
    }
}

