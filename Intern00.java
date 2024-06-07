import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.videoio.VideoCapture;
import org.opencv.core.*;
//import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class Intern00 extends JFrame {

    private JLabel cameraScreen;
    private JButton btnRecord;
    private VideoCapture capture;
    private Mat image;
    private boolean recording = false;
    private VideoWriter videoWriter;

    public Intern00() {

        setLayout(null);

        cameraScreen = new JLabel();
        cameraScreen.setBounds(0, 0, 640, 480);
        add(cameraScreen);

        btnRecord = new JButton("Record");
        btnRecord.setBounds(300, 480, 80, 40);
        add(btnRecord);

        btnRecord.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!recording) {
                    startRecording();
                    btnRecord.setText("Stop");
                } else {
                    stopRecording();
                    btnRecord.setText("Record");
                }
            }
        });

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (recording) {
                    stopRecording();
                }
                capture.release();
                image.release();
                System.exit(0);
            }
        });
        setSize(new Dimension(640, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void startRecording() {
        Integer fps;
        String FPS = JOptionPane.showInputDialog(this, "Enter FPS: ");
        if(FPS == null)
            fps = 25;
        else
            fps = Integer.parseInt(FPS);
            
        recording = true;
        capture = new VideoCapture(0);
        image = new Mat();
        Size frameSize = new Size((int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH),
                (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));

        String name = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss").format(new Date());
        String filename = name + ".mp4";
        videoWriter = new VideoWriter(filename, VideoWriter.fourcc('a', 'v', 'c', '1'), fps, frameSize, true);

        new Thread(new Runnable() {

            public void run() {
                while (recording) {
                    capture.read(image);
                    videoWriter.write(image);
                    ImageIcon icon = new ImageIcon(Mat2BufferedImage(image));
                    cameraScreen.setIcon(icon);
                }
            }
        }).start();
    }

    public void stopRecording() {
        recording = false;
        videoWriter.release();
    }

    public BufferedImage Mat2BufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b);
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        EventQueue.invokeLater(new Runnable() {

            public void run() {
                Intern00 proggie = new Intern00();
            }
        });
    }
}
