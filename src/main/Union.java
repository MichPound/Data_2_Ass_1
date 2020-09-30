package main;

public class Union {

    public static int find(int[] a, int id) {
        if(a[id]==id) return id;
        else return find(a,a[id]);
    }

    //q's root will reference p's root
    public static void union(int[] a, int p, int q) {
        a[find(a,q)]=find(a,p);
    }
}
