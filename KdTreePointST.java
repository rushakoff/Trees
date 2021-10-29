import edu.princeton.cs.algs4.MaxPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class KdTreePointST<Value> implements PointST<Value> {
    private Node root;
    private int N;
    // 2d-tree (generalization of a BST in 2d) representation.
    private class Node {
        private Point2D p;   // the point
        private Value val;   // the symbol table maps the point to this value
        private RectHV rect; // the axis-aligned rectangle corresponding to 
                             // this node
        private Node lb;     // the left/bottom subtree
        private Node rt;     // the right/top subtree

        // Construct a node given the point, the associated value, and the 
        // axis-aligned rectangle corresponding to the node.
        Node(Point2D p, Value val, RectHV rect) {
            this.p = p;
            this.val = val;
            this.rect = rect;
        }
    }

    // Construct an empty symbol table of points.
    public KdTreePointST() {
        root = null;
        N = 0;
    }

    // Is the symbol table empty?
    public boolean isEmpty() { 
        return (N == 0);
    }

    // Number of points in the symbol table.
    public int size() {
        return N;
    }

    // Associate the value val with point p.
    public void put(Point2D p, Value val) {
        root = put(root, p, val, null, true);
    }

    // Helper for put(Point2D p, Value val).
    private int count = 0;
    private Node put(Node x, Point2D p, Value val, RectHV rect, boolean lr) {
        if (x == null)
        {
            N++;
            x = new Node(p, val, rect);
        }else
        {
            if (lr)
            {
                if (p.x() < x.p.x())
                {
                    x.lb = put(x.lb, p, val, rect, false);
                }else if (p.x() > x.p.x())
                {
                    x.rt = put(x.rt, p, val, rect, false);
                }else
                {
                    x.val = val;
                }
            }else
            {
                if (p.y() < x.p.y())
                {
                    x.lb = put(x.lb, p, val, rect, true);
                }else if (p.y() > x.p.y())
                {
                    x.rt = put(x.rt, p, val, rect, true);
                }else
                {  
                    x.val = val;
                }
            }
        }
        return x;
    }

    // Value associated with point p.
    public Value get(Point2D p) {
         return get(root, p, true);
    }

    // Helper for get(Point2D p).
    private Value get(Node x, Point2D p, boolean lr) {
        if (x == null)
        {
            return null;
        }else
        {
            if (lr)
            {
                if (p.x() < x.p.x())
                {
                    return get(x.lb, p, false);
                }else if (p.x() > x.p.x())
                {
                    return get(x.rt, p, false);
                }else
                {
                    return x.val;
                }
            }else
            {
                if (p.y() < x.p.y())
                {
                    return get(x.lb, p, true);
                }else if (p.x() > x.p.x())
                {
                    return get(x.rt, p, true);
                }else
                {
                    return x.val;
                }
            }
        }
    }

    // Does the symbol table contain the point p?
    public boolean contains(Point2D p) {
        Node x = root;
        boolean lr = true;
        while ( x != null)
        {
            if (lr)
            {
                if (p.x() < x.p.x())
                {
                    x = x.lb;
                }else if (p.x() > x.p.x())
                {
                    x = x.rt;
                }else
                {
                    return true;
                }
            }else
            {
                if (p.y() < x.p.y())
                {
                    x = x.lb;
                }else if (p.y() > x.p.y())
                {
                    x = x.rt;
                }else
                {
                    return true;
                }
            }
            lr = !lr;
        }
        return false;
    }

    // All points in the symbol table, in level order.
    public Iterable<Point2D> points() {
        Queue<Node> lOrder = new Queue<Node>();
        lOrder.enqueue(root);
        Queue<Point2D> qPoints = new Queue<Point2D>();
        while (qPoints.size() != N)
        {   
            int lOSize = lOrder.size();
            for(int i = 0; i < lOSize; i++)
            {
                Node temp = lOrder.dequeue();
                qPoints.enqueue(temp.p);
                if (temp.lb != null){lOrder.enqueue(temp.lb);}
                if (temp.rt != null){lOrder.enqueue(temp.rt);}
            }
        }
        return qPoints;
    }

    // All points in the symbol table that are inside the rectangle rect.
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> rQueue = new Queue<Point2D>();
        range(root, rect, rQueue);
        return rQueue;
    }

    // Helper for public range(RectHV rect).
    private void range(Node x, RectHV rect, Queue<Point2D> q) {
        if (x == null){return;}
        if (rect.contains(x.p))
        {
            q.enqueue(x.p);
        }
        range(x.lb, rect, q);
        range(x.rt, rect, q);
    }

    // A nearest neighbor to point p; null if the symbol table is empty.
    public Point2D nearest(Point2D p) {
        if (isEmpty()) {return null;}
        return nearest(root, p, root.p, Double.MAX_VALUE, true);
        
    }
    
    // Helper for public nearest(Point2D p).
    private Point2D nearest(Node x, Point2D p, Point2D nearest, 
                            double nearestDistance, boolean lr) {
        if (x == null) {return nearest;}
        if (x.p.equals(p))
        {
            if (x.rt != null && x.lb != null)
            {
                if (p.distanceSquaredTo(x.rt.p) < p.distanceSquaredTo(x.lb.p))
                {
                    return nearest(x.rt, p, nearest, nearestDistance, !lr);
                }else
                {
                    return nearest(x.lb, p, nearest, nearestDistance, !lr);
                }
            }else if (x.rt == null)
            {
                return nearest(x.lb, p, nearest, nearestDistance, !lr);
            }else if (x.lb == null)
            {
                return nearest(x.rt, p, nearest, nearestDistance, !lr);
            }else
            {
                return nearest;
            }
        }else
        {
           
            if (p.distanceSquaredTo(x.p) < p.distanceSquaredTo(nearest))
            {
                return nearest(x.lb, p, nearest(x.rt, p, x.p, p.distanceSquaredTo(x.p), !lr), nearestDistance, !lr);
                
            }else
            {
                return nearest(x.lb, p, nearest(x.rt, p, nearest, nearestDistance, !lr), nearestDistance, !lr);
            }
            
        }
    }

    // k points that are closest to point p.
    public Iterable<Point2D> nearest(Point2D p, int k) {
        MaxPQ<Point2D> mpq = new MaxPQ<Point2D>(p.distanceToOrder());
        nearest(root, p, k, mpq, true);
        return mpq;
    }

    // Helper for public nearest(Point2D p, int k).
    private void nearest(Node x, Point2D p, int k, MaxPQ<Point2D> pq, 
                         boolean lr) {
        if (x == null || pq.size() > k) {return;}
        if (!x.p.equals(p))
        {
            pq.insert(x.p);
            if(pq.size() > k)
            {
                pq.delMax();
            }
        }
        nearest(x.rt, p, k, pq, !lr);
        nearest(x.lb, p, k, pq, !lr);
    }

    // Test client. [DO NOT EDIT]
    public static void main(String[] args) {
        KdTreePointST<Integer> st = new KdTreePointST<Integer>();
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
