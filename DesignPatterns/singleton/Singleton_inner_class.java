package singleton;

public class Singleton_inner_class {
   // save instance in a inner class

    private Singleton_inner_class(){}

    public static Singleton_inner_class getInstance() {
        return Holder.instance;
    }

    // static member is initialized when the class is loaded
    // but static inner class is not initialized
    private static class Holder {
        private static final Singleton_inner_class instance = new Singleton_inner_class();

   }
}
