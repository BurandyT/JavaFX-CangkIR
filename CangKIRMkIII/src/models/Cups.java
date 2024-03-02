package models;

public class Cups {
	private String cid;
	private String cname;
	private int cprice;
	public Cups(String cid, String cname, int cprice) {
		super();
		this.cid = cid;
		this.cname = cname;
		this.cprice = cprice;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public int getCprice() {
		return cprice;
	}
	public void setCprice(int cprice) {
		this.cprice = cprice;
	}	
}
