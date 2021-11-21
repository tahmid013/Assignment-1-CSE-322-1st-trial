package Threading;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Worker extends Thread {
    Socket socket;
    HashMap<String, String> map = new HashMap<>();
    ArrayList<String> Atleast_Once = new ArrayList<String>();
    ArrayList<String> ActiveUser = new ArrayList<String>();
    boolean logged_in = false;

    public Worker(Socket socket,HashMap<String, String>  map,ArrayList<String> Atleast_Once,ArrayList<String> ActiveUser )
    {
        this.socket = socket;
        this.map = map;
        this.Atleast_Once = Atleast_Once;
        this.ActiveUser = ActiveUser;
    }

    public void run()
    {
        // buffers
        try {
            ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());
            String St_id = "";
            String local_port_cl = "";
            Object lc;
            while (true)
            {

                if(!logged_in){
                    lc = in.readObject();
                    System.out.println("First incoming ... : "+lc);
                    local_port_cl = (String)  lc;
                    lc = in.readObject();
                    St_id = (String)lc;
                    System.out.println("Second incoming ... : "+lc);
                    System.out.println("(Client) Student id: "+St_id+" local port "+local_port_cl+" Requesting for login..." );
                }

                // checking
                //if(map.put(St_id, local_port_cl) == null)
                if( map.containsKey(St_id) && !logged_in)
                {
                    System.out.println("Login unsuccessful "+ St_id);
                    out.writeObject(false);
                }
                else {
                    out.writeObject(true);
                    String path = "";
                    if(!logged_in){
                        map.put(St_id, local_port_cl);
                    Atleast_Once.add(St_id);
                    ActiveUser.add(St_id);

                    path = System.getProperty("user.dir") + "\\folder\\" + St_id;
                    //Instantiate the File class
                    File f1 = new File(path + "\\public");
                    File f2 = new File(path + "\\private");
                    //Creating a folder using mkdirs() method
                    boolean bool1 = f1.mkdirs();
                    boolean bool2 = f2.mkdirs();
                    if (bool1 & bool2) {
                        System.out.println("Folder is created successfully");
                    } else {
                        System.out.println("Error Found!");
                    }
                }
                    logged_in = true;
                    lc = in.readObject();
                    System.out.println("Third incoming ... : "+lc);
                    int choice = (int)lc;

                    //upload
                    if(choice == 6)
                    {
                        try
                        {
                            lc = in.readObject();
                            System.out.println("Fourth incoming ... : "+lc);
                            int p_c = (int)lc;
                            if(p_c == 1) path+="\\public";
                            else path+="\\private";

                            lc = in.readObject();
                            System.out.println("Fifth incoming ... : "+lc);
                            String file_name = (String) lc;

                            lc = in.readObject();
                            System.out.println("Sixth incoming ... : "+lc);
                            String strRecv = (String) lc;					//These two lines are used to determine
                            int filesize=Integer.parseInt(strRecv);		//the size of the receiving file
                            byte[] contents = new byte[10000];
                            FileOutputStream fos = new FileOutputStream(path+"\\"+file_name);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            InputStream is = socket.getInputStream();

                            int bytesRead = 0;
                            int total=0;			//how many bytes read

                            while(total!=filesize)	//loop is continued until received byte=totalfilesize
                            {
                                bytesRead=is.read(contents);
                                total+=bytesRead;
                                bos.write(contents, 0, bytesRead);
                            }
                            bos.flush();
                            System.out.println("File transfered successful...");
                        }
                        catch(Exception e)
                        {
                            System.err.println("Could not transfer file.  "+ e);
                        }
                    }
                    else if(choice == 7)
                    {
                        logged_in = false;
                        System.out.println("Closing socket...for "+ St_id);

                       for (int i = 0; i < ActiveUser.size(); i++) {
                            if(ActiveUser.get(i) == St_id) {
                                ActiveUser.remove(i);
                                break;
                            }
                        }

                       // socket.close();
                        break;

                    }

                }

                for (Object objectName : map.keySet()) {
                    System.out.print(objectName+ " : ");
                    System.out.println(map.get(objectName));
                }

                Thread.sleep(1000);
                System.out.println("New...");

                //out.flush();
            }
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
