import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class main {
    static int[] x = {0};
    static int[] y = {0};

    static class RenderPanel extends JPanel {
        int[] indexBuffer;
        int bufferWidth, bufferHeight;



    }
public static void main(String[] args) {
    JFrame frame = new JFrame("3D Render");
    Container pane = frame.getContentPane();
    pane.setLayout(new BorderLayout());

    RenderPanel renderPanel = new RenderPanel(){
        
    
    public void paintComponent(Graphics g) {
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            ArrayList<Square> init = new ArrayList<>();
            //Triangle boilerplate----------------------------------
        init.add(new Square(new Vertex(100, 100, 100),
                        new Vertex(100, 100, -100),
                        new Vertex(100, -100, -100),
                        new Vertex(100, -100, 100), 
                        Color.WHITE));

        init.add(new Square(new Vertex(-100, 100, 100),
        new Vertex(-100, 100, -100),
        new Vertex(-100, -100, -100),
        new Vertex(-100, -100, 100), 
        Color.BLUE));



    init.add(new Square(new Vertex(100, 100, 100),
                        new Vertex(-100, 100, 100),
                        new Vertex(-100, 100, -100),
                        new Vertex(100, 100, -100),
                        Color.RED));

     init.add(new Square(new Vertex(100, -100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(-100, -100, -100),
                        new Vertex(100, -100, -100),
                        Color.GREEN));


     init.add(new Square(new Vertex(100, 100, 100),
                        new Vertex(-100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(100, -100, 100),
                        Color.YELLOW));

    init.add(new Square(new Vertex(100, 100, -100),
                        new Vertex(-100, 100, -100),
                        new Vertex(-100, -100, -100),
                        new Vertex(100, -100, -100),
                        Color.ORANGE));

        List<Square> tris = new ArrayList<>();
        
        List<Square> current = init;
        List<Square> next = new ArrayList<Square>();

        for (int i = 0; i < 3; i++)
        {
            next.clear();
            makeMoreTriangles(current, next);
            List<Square> temp = current;
            current = next;
            next = temp;
        }

        tris = current;
            //Triangle boilerplate----------------------------------

            
            

            //rotation boilerplate-------------------------------------

    double heading = Math.toRadians(x[0]);
            Matrix3 headingTransform = new Matrix3(new double[]{
                Math.cos(heading), 0, -Math.sin(heading),
                        0, 1, 0,
                        Math.sin(heading), 0, Math.cos(heading)
                    });
    double pitch = Math.toRadians(y[0]);
                        Matrix3 pitchTransform = new Matrix3(new double[]{
                                1, 0, 0,
                                0, Math.cos(pitch), Math.sin(pitch),
                                0, -Math.sin(pitch), Math.cos(pitch)
                        });
        // Merge matrices in advance
        Matrix3 transform = headingTransform.multiply(pitchTransform);



            //rotation boilerplate ^^^^^^^^^^^^^^




        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        g2.fillRect(0, 0, getWidth(), getHeight());
    
        
                     // The generated shape is centered on the origin (0, 0, 0), and we will do rotation around the origin later.
        // g2.translate(getWidth() / 2, getHeight() / 2);
        // g2.setColor(Color.white);
        // for (Triangle t : tris) {
        //     Path2D path = new Path2D.Double();
        //     path.moveTo(t.v1.x, t.v1.y);
        //     path.lineTo(t.v2.x, t.v2.y);
        //     path.lineTo(t.v3.x, t.v3.y);
        //     path.closePath();
        //     g2.draw(path);
        // }
    
       // g2.translate(getWidth() / 2, getHeight() / 2);
g2.setColor(Color.WHITE);

                    double[] zBuffer = new double[img.getWidth() * img.getHeight()];
                    indexBuffer = new int[img.getWidth() * img.getHeight()];
// initialize array with extremely far away depths
for (int q = 0; q < zBuffer.length; q++) {
    zBuffer[q] = Double.NEGATIVE_INFINITY;
    indexBuffer[q] = -1;



}
               
                    

                    for (int i =0; i < tris.size(); i++) {
                    Square t = tris.get(i);

                    Vertex v1 = transform.transform(t.v1);
                    Vertex v2 = transform.transform(t.v2);
                    Vertex v3 = transform.transform(t.v3);
                    Vertex v4 = transform.transform(t.v4);
                    v1.x += getWidth() / 2.0;
                    v1.y += getHeight() / 2.0;
                    v2.x += getWidth() / 2.0;
                    v2.y += getHeight() / 2.0;
                    v3.x += getWidth() / 2.0;
                    v3.y += getHeight() / 2.0;
                    v4.x += getWidth() / 2.0;
                    v4.y += getHeight() / 2.0;
                    // Calculate the range to be processed
                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, Math.min(v3.x, v4.x)))));
                    int maxX = (int) Math.min(img.getWidth() - 1,
                            Math.floor(Math.max(v1.x, Math.max(v2.x, Math.max(v3.x, v4.x)))));
                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, Math.min(v3.y, v4.y)))));
                    int maxY = (int) Math.min(img.getHeight() - 1,
                            Math.floor(Math.max(v1.y, Math.max(v2.y, Math.max(v3.y, v4.y)))));

                         Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
                    Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
                    // Normal vector
                    Vertex norm = new Vertex(
                        ab.y * ac.z - ab.z * ac.y,
                       ab.z * ac.x - ab.x * ac.z,
                        ab.x * ac.y - ab.y * ac.x
                    );
                    double normalLength =
                        Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
                    norm.x /= normalLength;
                    norm.y /= normalLength;
                norm.z /= normalLength;
                    double angleCos = Math.abs(norm.z);
                    t.color = getShade(t.color, angleCos);








                    for (int y = minY; y <= maxY; y++) {
                        for (int x = minX; x <= maxX; x++) {
                           rasterizeTriangle(v1, v2, v3, x, y, t, i, img, zBuffer, indexBuffer);
                        }
                    }

                    for (int y = minY; y <= maxY; y++) {
                        for (int x = minX; x <= maxX; x++) {
                           rasterizeTriangle(v1, v3, v4, x, y, t, i, img, zBuffer, indexBuffer);
                        }
                    }



               }

                            

                

                g2.drawImage(img, 0, 0, null);
    
    
    this.indexBuffer = indexBuffer;
    this.bufferWidth = img.getWidth();
    this.bufferHeight = img.getHeight();
    
    
    
    }

    };

    renderPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                double yi = 180.0 / renderPanel.getHeight();
                double xi = 180.0 / renderPanel.getWidth();
                x[0] = (int) (e.getX() * xi);
                y[0] = -(int) (e.getY() * yi);
                renderPanel.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }

        });

        renderPanel.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (renderPanel.indexBuffer == null) return; // not painted yet
        int cx = e.getX();
        int cy = e.getY();
        if (cx < 0 || cx >= renderPanel.bufferWidth || cy < 0 || cy >= renderPanel.bufferHeight) return;
        int idx = renderPanel.indexBuffer[cy * renderPanel.bufferWidth + cx];
        if (idx == -1) {
            System.out.println("No triangle at (" + cx + ", " + cy + ")");
        } else {
            System.out.println("Clicked triangle index: " + idx);
        }
    }
});

    

    pane.add(renderPanel, BorderLayout.CENTER);

    frame.setSize(600, 600);
    frame.setVisible((true));

    


}

    static boolean sameSide(Vertex A, Vertex B, Vertex C, Vertex p){
        Vertex V1V2 = new Vertex(B.x - A.x,B.y - A.y,B.z - A.z);
        Vertex V1V3 = new Vertex(C.x - A.x,C.y - A.y,C.z - A.z);
        Vertex V1P = new Vertex(p.x - A.x,p.y - A.y,p.z - A.z);

        // If the cross product of vector V1V2 and vector V1V3 is the same as the one of vector V1V2 and vector V1p, they are on the same side.
        // We only need to judge the direction of z
        double V1V2CrossV1V3 = V1V2.x * V1V3.y - V1V3.x * V1V2.y;
        double V1V2CrossP = V1V2.x * V1P.y - V1P.x * V1V2.y;

        return V1V2CrossV1V3 * V1V2CrossP >= 0;
}
 public static Color getShade(Color color, double shade) {

        double redLinear = Math.pow(color.getRed(), 2.2) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.2) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.2) * shade;

        int red = (int) Math.pow(redLinear, 1 / 2.2);
        int green = (int) Math.pow(greenLinear, 1 / 2.2);
        int blue = (int) Math.pow(blueLinear, 1 / 2.2);

        return new Color(red, green, blue);
}

public static void makeMoreTriangles(List<Square> init, List<Square> out)
{
    for (Square t : init) {
                Vertex m1 =
                        new Vertex((t.v1.x + t.v2.x) / 2, (t.v1.y + t.v2.y) / 2, (t.v1.z + t.v2.z) / 2);
                Vertex m2 =
                        new Vertex((t.v2.x + t.v3.x) / 2, (t.v2.y + t.v3.y) / 2, (t.v2.z + t.v3.z) / 2);
                Vertex m3 =
                        new Vertex((t.v3.x + t.v4.x) / 2, (t.v3.y + t.v4.y) / 2, (t.v3.z + t.v4.z) / 2);
                Vertex m4 =
                        new Vertex((t.v1.x + t.v4.x) / 2, (t.v1.y + t.v4.y) / 2, (t.v1.z + t.v4.z) / 2);
                Vertex center =
                        new Vertex((t.v1.x + t.v3.x) / 2, (t.v1.y + t.v3.y) / 2, (t.v1.z + t.v3.z) / 2);
                    
                out.add(new Square(t.v1, m1, center, m4, t.color));
                out.add(new Square(m1, t.v2, m2, center, t.color));
                out.add(new Square(center, m2, t.v3, m3, t.color));
                out.add(new Square(m4, center, m3, t.v4, t.color));
            }

       for (Square t : out) {
                for (Vertex v : new Vertex[]{t.v1, t.v2, t.v3, t.v4}) {
                    double l = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z) / Math.sqrt(30000);
                    v.x /= l;
                    v.y /= l;
                    v.z /= l;
                }
        }
    

}


public static void rasterizeTriangle(Vertex v1, Vertex v2, Vertex v3, int x, int y, Square t, int i, BufferedImage img, double[] zBuffer, int[] indexBuffer ){
     Vertex p = new Vertex(x,y,0);
                            // Judge once for each vertex
                            boolean V1 = sameSide(v1,v2,v3,p);
                            boolean V2 = sameSide(v2,v3,v1,p);
                            boolean V3 = sameSide(v3,v1,v2,p);
                            if (V3 && V2 && V1) {
                                //barycentric weights
                                double denom = (v2.y - v3.y) * (v1.x - v3.x) + (v3.x - v2.x) * (v1.y - v3.y);//twice the signed area of the triangle. the ratio will cancel in the next lines.
                                double w1 = ((v2.y - v3.y) * (x - v3.x) + (v3.x - v2.x) * (y - v3.y)) / denom;
                                double w2 = ((v3.y - v1.y) * (x - v3.x) + (v1.x - v3.x) * (y - v3.y)) / denom;
                                double w3 = 1 - w1 - w2;
                                double depth = w1 * v1.z + w2 * v2.z + w3 * v3.z;


                                int zIndex = y * img.getWidth() + x;
                                if (zBuffer[zIndex] < depth) {
                                img.setRGB(x, y, t.color.getRGB());
                                zBuffer[zIndex] = depth;
                                indexBuffer[zIndex] = i;
                                }

                                


                            }
    
}

}



