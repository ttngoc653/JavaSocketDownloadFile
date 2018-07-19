
public class TenFile implements Comparable<TenFile> {
	private int port;
	private String tennode;
	public TenFile() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public TenFile(int port, String tennode, String tenfile) {
		super();
		this.port = port;
		this.tennode = tennode;
		this.tenfile = tenfile;
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
	@Override
	public int compareTo(TenFile o) {
		// TODO Auto-generated method stub
		return this.tenfile.compareToIgnoreCase(o.getTenfile());
	}
}