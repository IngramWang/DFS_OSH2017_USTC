static public class File_Transfer{
    static public boolean sendfile(DataOutputStream dos,String file_name){
        try{
            int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];
            File f = new File(file_name);
            FileInputStream fis = new FileInputStream(f); 
            System.out.println("send file:"+file_name);
            //文件名和长度
            dos.writeUTF(f.getName());
            dos.flush();
            dos.writeLong(f.length());
            dos.flush();
            //传输文件
            byte[] sendBytes =new byte[1024];
            int length =0;
            while((length = fis.read(sendBytes,0, sendBytes.length)) >0){
                        dos.write(sendBytes,0, length);
                        dos.flush();
                }
            System.out.println("传输完成");
            fis.close();
            dos.close();
            return true;
            }catch (Exception e) {
                e.printStackTrace();
            }
         return true;
        }
    static public boolean receivefile(DataInputStream dis){
        try {
            //文件名和长度
            String fileName = dis.readUTF();
            long fileLength = dis.readLong();
            FileOutputStream fos =new FileOutputStream(new File(fileName));
                     
            byte[] sendBytes =new byte[1024];
            int transLen =0;
            System.out.println("----开始接收文件<" + fileName +">,文件大小为<" + fileLength +">----");
            while(true){
                int read =0;
                read = dis.read(sendBytes);
                if(read == -1)
                    break;
                transLen += read;
                System.out.println("接收文件进度" +100 * transLen/fileLength +"%...");
                fos.write(sendBytes,0, read);
                fos.flush();
                }
                System.out.println("----接收文件<" + fileName +">成功-------");
                if(dis !=null)
                    dis.close();
                if(fos !=null)
                    fos.close();
                return true;
            }catch (Exception e) {
                e.printStackTrace();
            }
            return true;
    }
}	