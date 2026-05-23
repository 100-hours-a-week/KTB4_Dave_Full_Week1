public class DataPrinter {
    private DataPrinter(){

    }

    public static void printData(Data[] data, String name){
        StringBuilder sb = new StringBuilder();
        sb.append(name)
        .append(" 영역 데이터\n")
        .append("| ");
        for(int i = 0; i < data.length; i++){
            if(data[i] != null) {
                sb.append(data[i].getName())
                .append(" : ")
                .append(data[i].getLiveTime());
            }
            else{
                sb.append("X : X");
            }
            sb.append(" | ");
        }
        System.out.println(sb);
    }
}
