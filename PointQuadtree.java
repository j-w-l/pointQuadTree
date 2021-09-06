import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, with children at the subdivided quadrants.
 * An iterative implementation of the ALGOL structure described in Finkel and Bentley's seminal work.
 * https://link.springer.com/article/10.1007/BF00288933
 *
 * Created: 1/31/2020. Revised: 6/13/2021.
 * @author Jonathan Lee.
 * Indebted to a scaffold by CBK.
 *
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters.
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
//		To insert point p2, which is at (x,y):
		// If (x,y) is in quadrant 1
		boolean xCond = p2.getX() >= point.getX() && p2.getX() <= this.getX2();
		boolean yCond = p2.getY() <= point.getY() && p2.getY() >= this.getY1();
		if (xCond && yCond) {
			// If child 1 exists, then insert p2 in child 1.
			if (c1 != null) {
				this.c1.insert(p2);
			}

			// Else set child 1 to a new tree holding just p2.
			else {
				c1 = new PointQuadtree(p2, (int)point.getX(), this.getY1(), this.getX2(), (int)point.getY());
			}
		}

		// Elif (x,y) is in quadrant 2
		xCond = p2.getX() >= this.getX1() && p2.getX() <= point.getX();
		yCond = p2.getY() >= this.getY1() && p2.getY() <= point.getY();
		if (xCond && yCond) {
			// If child 1 exists, then insert p2 in child 1.
			if (c2 != null) {
				this.c2.insert(p2);
			}

			// Else set child 1 to a new tree holding just p2.
			else {
				c2 = new PointQuadtree(p2, this.getX1(), this.getY1(), (int)point.getX(), (int)point.getY());
			}
		}

		// Elif (x,y) is in quadrant 3
		xCond = p2.getX() >= this.getX1() && p2.getX() <= point.getX();
		yCond = p2.getY() >= point.getY() && point.getY() <= this.getY2();
		if (xCond && yCond) {
			// If child 1 exists, then insert p2 in child 1.
			if (c3 != null) {
				this.c3.insert(p2);
			}

			// Else set child 1 to a new tree holding just p2.
			else {
				c3 = new PointQuadtree(p2, this.getX1(), (int)point.getY(), (int)point.getX(), this.getY2());
			}
		}

		// Elif (x,y) is in quadrant 4
		xCond = p2.getX() >= point.getX() && p2.getX() <= this.getX2();
		yCond = p2.getY() >= point.getY() && point.getY() <= this.getY2();
		if (xCond && yCond) {
			// If child 1 exists, then insert p2 in child 1.
			if (c4 != null) {
				this.c4.insert(p2);
			}

			// Else set child 1 to a new tree holding just p2.
			else {
				c4 = new PointQuadtree(p2, (int)point.getX(), (int)point.getY(), this.getX2(), this.getY2());
			}
		}

	}

	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		return allPoints().size();
	}

	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		List<E> all = new ArrayList<E>();
		this.allHelper(all);
		return all;
	}

	private void allHelper(List<E> all) {
		all.add(point);
		// For each quadrant with a child, recurse with that child.
		if (hasChild(1)) {
			c1.allHelper(all);
		}
		if (this.hasChild(2)) {
			c2.allHelper(all);
		}
		if (this.hasChild(3)) {
			c3.allHelper(all);
		}
		if (this.hasChild(4)) {
			c4.allHelper(all);
		}
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
// 		To find all points within the circle (cx,cy,cr), stored in a tree covering rectangle (x1,y1)-(x2,y2)
		List<E> hits = new ArrayList<E>();
		this.finderHelper(cx, cy, cr, hits);
		return hits;

	}

	public boolean circleIntersectsRectangle(double cx, double cy, double cr, double x1, double y1, double x2, double y2) {
		return (cx-Math.min(Math.max(cx, x1), x2))*(cx-Math.min(Math.max(cx, x1), x2)) + (cy-Math.min(Math.max(cy, y1), y2))*(cy-Math.min(Math.max(cy, y1), y2)) <= cr*cr;
	}

	public boolean pointInCircle(double px, double py, double cx, double cy, double cr) {
		return (px-cx)*(px-cx) + (py-cy)*(py-cy) <= cr*cr;
	}

	private void finderHelper(double cx, double cy, double cr, List<E> hits) {
//		If the circle intersects the rectangle
		if (circleIntersectsRectangle(cx, cy, cr, getX1(), getY1(), getX2(), getY2())) {
//			If the tree's point is in the circle, then the blob is a "hit"
			if (pointInCircle(getPoint().getX(), getPoint().getY(), cx, cy, cr)) {
				hits.add(this.point);
			}
//			For each quadrant with a child, recurse with that child.
			if (hasChild(1)) {
				c1.finderHelper(cx, cy, cr, hits);
			}
			if (this.hasChild(2)) {
				c2.finderHelper(cx, cy, cr, hits);
			}
			if (this.hasChild(3)) {
				c3.finderHelper(cx, cy, cr, hits);
			}
			if (this.hasChild(4)) {
				c4.finderHelper(cx, cy, cr, hits);
			}
		}
	}

}
