package GxPtProxy.Bean;


public class Query {
    public String page="";
    public String max="";
    public Query(String page,String max){
        this.page = page;
        this.max = max;
    }
    public static class Fp extends Query{
        public String rz="";
        public String ksrq="";
        public String jsrq="";
        public Fp(String page,String max,String rz,String ksrq,String jsrq){
            super(page,max);
            this.rz = rz;
            this.ksrq = ksrq;
            this.jsrq = jsrq;
        }
    }
    public static class GxRz extends Query{
        public enum State{
            GX_N_QR,GX_QR
        };
        public State state;
        public GxRz(String page,String max,State state){
            super(page,max);
            this.state = state;
        }
        public GxRz(String page,String max,String state){
            super(page,max);
            this.state = State.valueOf(state);
        }
    }
    public static class Dkcx extends Query{
        public String tjyf="";
        public String xfsbh="";
        public String qrrzrq_q="";
        public String qrrzrq_z="";
        public Dkcx(String page,String max,String tjyf,String xfsbh,String qrrzrq_q,String qrrzrq_z){
            super(page,max);
            this.tjyf = tjyf;
            this.xfsbh= xfsbh;
            this.qrrzrq_q = qrrzrq_q;
            this.qrrzrq_z = qrrzrq_z;
        }
    }
}
