import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
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
        // Persists across repaints -- tracks which triangle indices have
        // been clicked and toggled to earthGreen. Without storing this
        // outside paintComponent, every repaint (e.g. every slider move)
        // rebuilds `tris` from scratch and would silently wipe out any
        // manual color toggles.
        java.util.Set<Integer> toggledGreen = new java.util.TreeSet<>();



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
            Color earthBlue = new Color(52, 102, 235);
            Color earthGreen = new Color(14, 204, 52);

        init.add(new Square(new Vertex(100, 100, 100),
                        new Vertex(100, 100, -100),
                        new Vertex(100, -100, -100),
                        new Vertex(100, -100, 100), 
                        earthBlue));

        init.add(new Square(new Vertex(-100, 100, 100),
        new Vertex(-100, 100, -100),
        new Vertex(-100, -100, -100),
        new Vertex(-100, -100, 100), 
        earthBlue));



    init.add(new Square(new Vertex(100, 100, 100),
                        new Vertex(-100, 100, 100),
                        new Vertex(-100, 100, -100),
                        new Vertex(100, 100, -100),
                        earthBlue));

     init.add(new Square(new Vertex(100, -100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(-100, -100, -100),
                        new Vertex(100, -100, -100),
                        earthBlue));


     init.add(new Square(new Vertex(100, 100, 100),
                        new Vertex(-100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(100, -100, 100),
                        earthBlue));

    init.add(new Square(new Vertex(100, 100, -100),
                        new Vertex(-100, 100, -100),
                        new Vertex(-100, -100, -100),
                        new Vertex(100, -100, -100),
                        earthBlue));

        List<Square> tris = new ArrayList<>();

        // Grid resolution: each face is split directly into gridResolution x
        // gridResolution sub-squares. This gives linear control over total
        // triangle count (6 * gridResolution^2), unlike the old recursive
        // 4x-per-pass subdivision, which could only produce 6*4^n squares --
        // jumping straight from 384 to 1536 with no values in between.
        // Tune this single number to dial in exactly the density you want:
        //   gridResolution=4 -> 96 squares    gridResolution=7 -> 294 squares
        //   gridResolution=5 -> 150 squares   gridResolution=8 -> 384 squares
        //   gridResolution=6 -> 216 squares   gridResolution=9 -> 486 squares
        int gridResolution = 12;
        for (Square face : init) {
            subdivideFace(face, gridResolution, tris);
        }



        int[] arr = {   786, 787, 788, 789, 790, 791, 798
        ,6, 18, 30, 42, 54, 66, 
        140, 141, 727, 728, 729, 730, 737,  739, 740, 741, 744, 745, 746, 747, 748, 749, 751, 752, 753, 757, 758, 759, 760, 761, 763, 764, 765, 770, 771, 772, 773, 775, 776, 799,
        119, 143, 273, 274, 284, 285, 286, 287, 440, 443, 454, 455, 466, 467, 478, 479, 491, 513, 514, 515, 525, 526, 527, 537, 538, 539, 549, 550, 551, 561, 562, 563, 573, 574, 575, 755, 767, 779, 801, 802, 812, 813, 824, 826, 838, 850, 851, 861, 862, 863,
        0, 2, 3, 4, 5, 7, 8, 12, 13, 14, 15, 16, 17, 19, 20, 24, 25, 26, 27, 28, 29, 31, 36, 37, 38, 39, 40, 41, 49, 50, 51, 52, 53, 63, 64, 288, 289, 290, 301, 302, 313, 314, 326, 579, 580, 581, 583
        , 444, 445, 446, 456, 457, 458, 468, 469, 470, 480, 481, 482, 504, 505, 506, 516, 517, 647, 659, 671
        ,738, 750, 762, 447, 436, 492, 493, 506, 582, 595, 608, 620, 633, 646, 657, 670, 774
        ,11, 22,  599, 611, 623, 635, 432, 433, 459, 471, 483, 494, 495, 507, 518, 519, 528, 529, 531
            , 139, 262, 460, 461, 462, 472, 473, 484, 496, 497, 508, 726, 783, 784, 785, 520, 521, 533, 534, 453, 502
    , 185, 201, 202, 208, 211, 213, 214, 215, 221, 223, 224, 225, 226, 227, 234, 236, 237, 238, 239, 249, 250, 251, 261, 263, 272, 275, 283, 849, 860,
    177, 178, 188, 239, 555, 556, 557, 559, 560, 568, 569, 570, 571, 572, 548,
    144, 145, 148, 156, 157, 158, 161, 168, 169, 282,  180, 396, 408, 409, 420, 421, 422, 423, 548, 672, 684, 685, 696, 697, 708, 709, 710
    };
        

        for (int i: arr)
        {
        tris.get(i).color = earthGreen;
        }

        // Reapply any colors toggled by clicking. `tris` was just rebuilt
        // from scratch above, so without this, every repaint would forget
        // every toggle and fall back to earthBlue (or the two hardcoded
        // earthGreen indices above).
        for (int idx : toggledGreen) {
            if (idx >= 0 && idx < tris.size()) {
                tris.get(idx).color = earthGreen;
            }
        }

        double targetRadius = Math.sqrt(30000);
java.util.Set<Vertex> warped =
    java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());

for (Square t : tris) {
    for (Vertex v : new Vertex[]{t.v1, t.v2, t.v3, t.v4}) {
        if (!warped.add(v)) continue; // already warped this exact object -- skip

        double cx = v.x / 100.0;
        double cy = v.y / 100.0;
        double cz = v.z / 100.0;

        double x2 = cx * cx, y2 = cy * cy, z2 = cz * cz;
        double wx = cx * Math.sqrt(1 - y2 / 2 - z2 / 2 + y2 * z2 / 3);
        double wy = cy * Math.sqrt(1 - z2 / 2 - x2 / 2 + z2 * x2 / 3);
        double wz = cz * Math.sqrt(1 - x2 / 2 - y2 / 2 + x2 * y2 / 3);

        v.x = wx * targetRadius;
        v.y = wy * targetRadius;
        v.z = wz * targetRadius;
    }
}
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

    // Mouse dragging no longer drives rotation -- only clicking, to print
    // the clicked triangle's index. Rotation is now driven exclusively by
    // the two sliders added below.
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
            return;
        }

        // Toggle: if this index was already marked green, un-mark it
        // (falls back to earthBlue / whatever it would otherwise be);
        // otherwise mark it green. Set.remove() returns true only if the
        // element was present, which is what makes this a clean toggle.
        if (!renderPanel.toggledGreen.remove(idx)) {
            renderPanel.toggledGreen.add(idx);
        }

        // Print the full, current list of toggled indices on every click --
        // TreeSet keeps them in sorted order, so this is ready to copy
        // straight into a hardcoded ArrayList/int[] later.
        System.out.println("Clicked index: " + idx + "  |  All toggled indices: " + renderPanel.toggledGreen);

        renderPanel.repaint();
    }
});

    // Horizontal rotation slider, placed along the bottom.
    // Range 0-360 degrees maps directly onto the "heading" value (x[0])
    // already used by paintComponent's headingTransform.
    JSlider headingSlider = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
    headingSlider.addChangeListener(e -> {
        x[0] = headingSlider.getValue();
        renderPanel.repaint();
    });

    // Vertical rotation slider, placed along the right side.
    // Range -90 to 90 degrees maps onto the "pitch" value (y[0]) used by
    // paintComponent's pitchTransform. JSlider is oriented VERTICAL so it
    // visually runs top-to-bottom along the right edge of the window.
    JSlider pitchSlider = new JSlider(JSlider.VERTICAL, -90, 90, 0);
    pitchSlider.addChangeListener(e -> {
        y[0] = pitchSlider.getValue();
        renderPanel.repaint();
    });

    pane.add(renderPanel, BorderLayout.CENTER);
    pane.add(headingSlider, BorderLayout.SOUTH);
    pane.add(pitchSlider, BorderLayout.EAST);

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

// Bilinear interpolation across a flat quad. (u, v) each range 0..1, with
// v1=(0,0), v2=(1,0), v3=(1,1), v4=(0,1) in that parameter space -- matching
// the corner order already used everywhere else (v1,v2,v3,v4 going around
// the quad). This lets us find any point on the face directly, instead of
// only ever being able to find exact midpoints via recursive halving.
static Vertex bilerp(Vertex v1, Vertex v2, Vertex v3, Vertex v4, double u, double v) {
    double x = (1-u)*(1-v)*v1.x + u*(1-v)*v2.x + u*v*v3.x + (1-u)*v*v4.x;
    double y = (1-u)*(1-v)*v1.y + u*(1-v)*v2.y + u*v*v3.y + (1-u)*v*v4.y;
    double z = (1-u)*(1-v)*v1.z + u*(1-v)*v2.z + u*v*v3.z + (1-u)*v*v4.z;
    return new Vertex(x, y, z);
}

// Splits a single flat face into an n x n grid of sub-squares in one pass,
// using bilerp to compute every grid point directly. This produces exactly
// the same flat-grid positions that repeated recursive halving would at
// this resolution -- it's just computed directly instead of built up
// through log2(n) doubling steps, which is what gives linear (not only
// powers-of-4) control over subdivision density.
static void subdivideFace(Square face, int n, List<Square> out) {
    Vertex[][] grid = new Vertex[n + 1][n + 1];
    for (int i = 0; i <= n; i++) {
        for (int j = 0; j <= n; j++) {
            double u = (double) i / n;
            double v = (double) j / n;
            grid[i][j] = bilerp(face.v1, face.v2, face.v3, face.v4, u, v);
        }
    }
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            out.add(new Square(grid[i][j], grid[i+1][j], grid[i+1][j+1], grid[i][j+1], face.color));
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