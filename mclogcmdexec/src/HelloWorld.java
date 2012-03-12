public class HelloWorld {
    public static void main( String[] args ) {
        try {
            System.out.println( "Hello World" );
        }
        catch ( Throwable t ) {
            t.printStackTrace( System.err );
        }
    }
}
