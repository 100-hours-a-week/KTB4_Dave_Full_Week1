public class DataPrinter {
    private DataPrinter(){

    }

    public static void printData(Data[] data, String name){
        System.out.println(name +" 영역 데이터");
        System.out.print("| ");
        for(int i = 0; i < data.length; i++){
            if(data[i] != null) {
                System.out.print(data[i].getName()+" : " + data[i].getLiveTime() +" | ");
            }
            else{
                System.out.print("X : X | ");
            }
        }
        System.out.println();
    }
}
