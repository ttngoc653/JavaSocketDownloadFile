
public class TenFile {

    private int stt;
    private int port;
    private String tennode;

    public TenFile() {
        super();
        // TODO Auto-generated constructor stub
    }

    public TenFile(int stt, int port, String tennode, String tenfile) {
        super();
        this.stt = stt;
        this.port = port;
        this.tennode = tennode;
        this.tenfile = tenfile;
    }

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTennode() {
        return tennode;
    }

    public void setTennode(String tennode) {
        this.tennode = tennode;
    }

    public String getTenfile() {
        return tenfile;
    }

    public void setTenfile(String tenfile) {
        this.tenfile = tenfile;
    }
    private String tenfile;

}
