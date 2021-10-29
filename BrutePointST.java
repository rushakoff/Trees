import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class BrutePointST<Value> implements PointST<Value> {
    private Queue<Point2D> key;
    private Queue<Value> value;
    
    // Construct an empty symbol table of points.
    public BrutePointST() {
        key = new Queue<Point2D>();
        value = new Queue<Value>();
    }

    // Is the symbol table empty?
    public boolean isEmpty() { 
        return key.size() == 0;
    }

    // Number of points in the symbol table.
    public int size() {
        return key.size();
    }

    // Associate the value val with point p.
    public void put(Point2D p, Value val) {
        key.enqueue(p);
        value.enqueue(val);
    }

    // Value associated with point p.
    public Value get(Point2D p) {
        int i = 0;
        int j = 0;
        for (Point2D p2 : key)
        { 
            
            if (p2 == p) {break;}
            i++;
        }
        for (Value v : value)
        {
            if (i >= key.size()) {break;}
            if (j == i) {return v;}
            j++;
        }        
        return null;
    }

    // Does the symbol table contain the point p?
    public boolean contains(Point2D p) {
        for (Point2D p2 : key)
        { 
            if (p2.equals(p)) {return true;}
        }
        return false;
    }

    // All points in the symbol table.
    public Iterable<Point2D> points() {
        return key;
    }

    // All points in the symbol table that are inside the rectangle rect.
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> tempKey = new Queue<Point2D>();
        for (Point2D p2 : key)
        {
            if (rect.contains(p2))
            {
                tempKey.enqueue(p2);
            }
        }
        return tempKey;
    }

    // A nearest neighbor to point p; null if the symbol table is empty.
    public Point2D nearest(Point2D p) {
        if (isEmpty()){return null;}
        Point2D closest = key.peek();
        for (Point2D p2 : key)
        {
            if (p.distanceSquaredTo(p2) < p.distanceSquaredTo(closest))
            {
                if (!p2.equals(p))
                {
                    closest = p2;
                }
            }
        }
        return closest;
    }

    // k points that are closest to point p.
    public Iterable<Point2D> nearest(Point2D p, int k) {
        if (isEmpty()){return null;}
        Queue<Point2D> tmpKey = new Queue<Point2D>();
        Queue<Point2D> tmpKey2 = new Queue<Point2D>();
        Queue<Point2D> finalKey = new Queue<Point2D>();
        for (Point2D p2 : key)
        {
            tmpKey.enqueue(p2);
        }
        
        for (int i = 0; i < k; i++)
        {
            Point2D closest = tmpKey.dequeue();
            int tkSize = tmpKey.size();
            for (int j = 0; j < tkSize; j++)
            {
                Point2D p2 = tmpKey.dequeue();
                if (p2.equals(p)) {continue;}
                if (p.distanceSquaredTo(p2) < p.distanceSquaredTo(closest))
                {
                    tmpKey2.enqueue(closest);
                    closest = p2;
                }else
                {
                    tmpKey2.enqueue(p2);
                }
            }
            
            int tk2Size = tmpKey2.size();
            for(int x = 0; x < tk2Size; x++)
            {
                tmpKey.enqueue(tmpKey2.dequeue());
            }
            finalKey.enqueue(closest);
        }
        
        return finalKey;
    }

    // Test client. [DO NOT EDIT]
    public static void main(String[] args) {
        BrutePointST<Integer> st = new BrutePointST<Integer>();
        double qx = Double.parseDouble(args[0]);
        double qy = Double.parseDouble(args[1]);
        double rx1 = Double.parseDouble(args[2]);
        double rx2 = Double.parseDouble(args[3]);
        double ry1 = Double.parseDouble(args[4]);
        double ry2 = Double.parseDouble(args[5]);
        int k = Integer.parseInt(args[6]);
        Point2D query = new Point2D(qx, qy);
        RectHV rect = new RectHV(rx1, ry1, rx2, ry2);
        int i = 0;
        while (!StdIn.isEmpty()) {
            double x = StdIn.readDouble();
            double y = StdIn.readDouble();
            Point2D p = new Point2D(x, y);
            st.put(p, i++);
        }
        StdOut.println("st.empty()? " + st.isEmpty());
        StdOut.println("st.size() = " + st.size());
        StdOut.println("First " + k + " values:");
        i = 0;
        for (Point2D p : st.points()) {
            StdOut.println("  " + st.get(p));
            if (i++ == k) {
                break;
            }
        }
        StdOut.println("st.contains(" + query + ")? " + st.contains(query));
        StdOut.println("st.range(" + rect + "):");
        for (Point2D p : st.range(rect)) {
            StdOut.println("  " + p);
        }
        StdOut.println("st.nearest(" + query + ") = " + st.nearest(query));
        StdOut.println("st.nearest(" + query + ", " + k + "):");
        for (Point2D p : st.nearest(query, k)) {
            StdOut.println("  " + p);
        }
    }
}
