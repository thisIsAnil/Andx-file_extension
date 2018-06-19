package andx;

import java.util.ArrayList;
import java.util.List;

class Databytes {
        List<byte[]> bytes=new ArrayList<>();
        public int size(){

            int size=0;
            for(byte[] b:bytes){
                size+=b.length;
            }
            return size;
        }
    }
   