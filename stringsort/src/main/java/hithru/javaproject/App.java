package hithru.javaproject;

public class App
{
    public static void main( String[] args )
    {
        String[] actualNames = new String[]{"Hithru","Dilum","Kaveesha"};
        Sort.sort(actualNames);
        for (final String name : actualNames) {
            System.out.println(name);
        }
    }
}