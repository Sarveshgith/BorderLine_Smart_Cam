import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.videoio.VideoCapture;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

public class OpenCVtest extends JFrame{

    private JLabel cameraScreen;
    private JButton btnCapture;
    private VideoCapture capture;
    private Mat image;
    private boolean clicked = false;

    public OpenCVtest(){

        setLayout(null);

        cameraScreen = new JLabel();
        cameraScreen.setBounds(0, 0, 640, 480);
        add(cameraScreen);

        btnCapture = new JButton("Capture");
        btnCapture.setBounds(300, 480, 80, 40);
        add(btnCapture);

        btnCapture.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e){
                clicked = true;
            }
        });

        addWindowListener(new WindowAdapter() {
            
            public void windowClosing(WindowEvent e){
                super.windowClosing(e);
                capture.release();
                image.release();
                System.exit(0);
            }
        });;
        setSize(new Dimension(640, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void startCamera(){
        capture = new VideoCapture(0);
        image = new Mat();
        byte[] imageData;

        ImageIcon icon;

        while(true){
            capture.read(image);

            final MatOfByte buf = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, buf);

            imageData = buf.toArray();
            icon = new ImageIcon(imageData);
            cameraScreen.setIcon(icon);
            if(clicked){
                String name = JOptionPane.showInputDialog(this, "Enter Image Name");
                if(name == null){
                    name = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss").format(new Date());
                }

                Imgcodecs.imwrite("images/" + name + ".jpg", image);
                clicked = false;
            }
        }
    }


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        EventQueue.invokeLater(new Runnable() {

            public void run(){
               OpenCVtest opencv = new OpenCVtest(); 

               new Thread(new Runnable() {
                
                public void run(){
                    opencv.startCamera();
                }
               }).start();
            }
        });
        //System.out.println("load success");
    }
}
