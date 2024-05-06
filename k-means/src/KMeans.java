import java.util.*;

public class KMeans {

    private int k, size;
    private List<MyObject> data;
    private List<double[]> centroids;
    private List<List<MyObject>> groups;

    public KMeans(int k, List<MyObject> data) {
        this.data = data;
        this.size = data.get(0).parameters.length;
        this.k = k;
        this.centroids = initialize();
        this.groups = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            groups.add(new ArrayList<>());
        }
        masterLoop();
//        displayClusters();
        displayEntropy();
    }

    /**
     * Initializes the centroids randomly.
     *
     * @return The list of initial centroids.
     */
    private List<double[]> initialize() {
        List<double[]> centroids = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < k; i++) {
            int index = random.nextInt(data.size());
            double[] centroid = data.get(index).parameters;
            centroids.add(centroid);
        }
        return centroids;
    }

    /**
     * Runs the KMeans algorithm until convergence.
     */
    private void masterLoop() { // to be changed to masterLoop()
        boolean change_happened;
        boolean isThereAnEmpty;
        do {
            chooseGroup();
            isThereAnEmpty = checkForEmpty();
            if (isThereAnEmpty) {
                fillEmpty();
            }

            List<double[]> newCentroids = updateCentroids();
            change_happened = !compareCentroids(centroids, newCentroids);
            centroids = newCentroids;

            displaySumOfDistances();
            displayPurity();

        } while (change_happened || isThereAnEmpty);
    }

    /**
     * Checks if there are any empty groups.
     *
     * @return True if there is at least one empty group, false otherwise.
     */
    private boolean checkForEmpty() {
        for (List<MyObject> g : groups) {
            if (g.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fills the empty groups by taking an object from a non-empty group.
     */
    private void fillEmpty() {
        List<MyObject> emptyGroup = null;
        List<MyObject> nonEmptyGroup = null;

        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).isEmpty()) {
                emptyGroup = groups.get(i);
            } else {
                nonEmptyGroup = groups.get(i);
            }
            if (emptyGroup != null && nonEmptyGroup != null) {
                break;
            }
        }

        if (emptyGroup != null && nonEmptyGroup != null) {
            MyObject myObject = nonEmptyGroup.remove(nonEmptyGroup.size() - 1);
            emptyGroup.add(myObject);
        }
    }

    /**
     * Compares two lists of centroids.
     *
     * @param cen1 The first list of centroids.
     * @param cen2 The second list of centroids.
     * @return True if the two lists are equal, false otherwise.
     */
    private boolean compareCentroids(List<double[]> cen1, List<double[]> cen2) {
        if (cen1.size() != cen2.size()) {
            return false;
        }
        for (int i = 0; i < cen1.size(); i++) {
            if (!Arrays.equals(cen1.get(i), cen2.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the centroid of a group.
     *
     * @param grupa The group for which to calculate the centroid.
     * @return The centroid of the group.
     */
    private double[] countCentroid(List<MyObject> grupa) {
        double[] centroid = new double[size];
        for (MyObject myObject : grupa) {
            for (int i = 0; i < size; i++) {
                centroid[i] += myObject.parameters[i];
            }
        }
        for (int i = 0; i < size; i++) {
            centroid[i] /= grupa.size();
        }
        return centroid;
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param point1 The first point.
     * @param point2 The second point.
     * @return The Euclidean distance between the two points.
     */
    private double distance(double[] point1, double[] point2) {
        double distance = 0;
        for (int i = 0; i < point1.length; i++) {
            distance += Math.pow(point1[i] - point2[i], 2); // eukalidesowa
        }
        return Math.sqrt(distance);
    }

    /**
     * Assigns each object to the closest centroid.
     */
    private void chooseGroup() {
        groups.forEach(List::clear);
        for (MyObject myObject : data) {
            int closestCentroid_index = closestCentroid(myObject.parameters);
            groups.get(closestCentroid_index).add(myObject);
        }
    }

    /**
     * Finds the index of the closest centroid to a given point.
     *
     * @param attributes The point for which to find the closest centroid.
     * @return The index of the closest centroid.
     */
    private int closestCentroid(double[] attributes) {
        int closest_index = 0;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < centroids.size(); i++) {
            double distance = distance(attributes, centroids.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                closest_index = i;
            }
        }
        return closest_index;
    }

    /**
     * Updates the centroids by calculating the mean of each group.
     *
     * @return The new centroids.
     */
    private List<double[]> updateCentroids() {
        List<double[]> newCentroids = new ArrayList<>();
        for (List<MyObject> g : groups) {
            double[] centroid = countCentroid(g);
            newCentroids.add(centroid);
        }
        return newCentroids;
    }

    /**
     * Displays the sum of distances from observations to the centroids of their assigned clusters.
     */
    private void displaySumOfDistances() {
        double sum = 0;
        for (int i = 0; i < groups.size(); i++) {
            for (MyObject myObject : groups.get(i)) {
                sum += distance(myObject.parameters, centroids.get(i));
            }
        }
        System.out.println("Sum of distances: " + String.format("%.2f", sum));
    }

    /**
     * Displays the clusters.
     */
    private void displayClusters() {
        System.out.println();
        for (int i = 0; i < groups.size(); i++) {
            System.out.println("GR." + (i + 1) + ":");
            for (MyObject myObject : groups.get(i)) {
                System.out.println(Arrays.toString(Arrays.stream(myObject.parameters).toArray()) + " " + myObject.kind);
            }
            System.out.println();
        }
    }

    /**
     * Displays the purity of each group.
     */
    private void displayPurity() {
        List<String> kinds = getKind();

        for (int i = 0; i < groups.size(); i++) {
            String txt = "";
            for (String kind : kinds) {
                int count = howManyOfKind(groups.get(i), kind);
                if (count > 0) {
                    txt += kind + " -> " + String.format("%.2f", (double) count / groups.get(i).size() * 100) + "% ";
                }
            }

            System.out.println("Klaster " + (i + 1) + ": " + txt);
        }
        System.out.println();
    }

    // ENTROPIA // ENTROPIA // ENTROPIA // ENTROPIA // ENTROPIA // ENTROPIA // ENTROPIA //

    /**
     * Displays the entropy of each group.
     */
    private void displayEntropy() {
        for (int i = 0; i < groups.size(); i++) {
            double entropy = countEntropy(groups.get(i));
            System.out.println("Entropia [GR." + (i + 1) + "] : " + String.format("%.2f", entropy));
        }
    }

    /**
     * Calculates the entropy of a group.
     *
     * @param group The group for which to calculate the entropy.
     * @return The entropy of the group.
     */
    private double countEntropy(List<MyObject> group) {
        double entropy = 0;
        if (group.isEmpty()) {
            return 0;
        }

        for (String kind : getKind()) {
            int count = howManyOfKind(group, kind);
            if (count > 0) {
                double prawdop = (double) count / group.size();
                entropy -= prawdop * Math.log(prawdop) / Math.log(2);
            }
        }
        return entropy;
    }

    /**
     * Counts the number of objects of a certain kind in a group.
     *
     * @param group The group in which to count the objects.
     * @param kind  The kind of objects to count.
     * @return The number of objects of the given kind in the group.
     */
    private int howManyOfKind(List<MyObject> group, String kind) {
        int count = 0;
        for (MyObject myObject : group) {
            if (myObject.kind.equals(kind)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the kinds of objects in the data.
     *
     * @return A list of the kinds of objects in the data.
     */
    private List<String> getKind() {
        List<String> kinds = new ArrayList<>();
        for (MyObject myObject : data) {
            String kind = myObject.kind;
            if (!kinds.contains(kind)) {
                kinds.add(kind);
            }
        }
        return kinds;
    }
}