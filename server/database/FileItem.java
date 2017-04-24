package database;

public class FileItem {
	
	//以下变量依次为
	//文件ID（碎片前缀）|文件名|文件逻辑路径|文件属性|修改时间|碎片数量|文件or文件夹	
	private int id;
	private String name;
	private String path;
	private String attribute;
	private String time;
	private int noa;
	private boolean isFolder;
	
	//构造函数，只能在包内调用
	FileItem(int id,String name,String path,String attribute,String time,int noa,boolean isFolder) {
		this.id=id;
		this.name=name;
		this.path=path;
		this.attribute=attribute;
		this.time=time;
		this.noa=noa;
		this.isFolder=isFolder;
	}
	
	//构造函数，参数为除了ID外的全部属性
	public FileItem(String name,String path,String attribute,String time,int noa,boolean isFolder){
		this.id=0;
		this.name=name;
		this.path=path;
		this.attribute=attribute;
		this.time=time;
		this.noa=noa;
		this.isFolder=isFolder;
	}

	//各类getter与setter，注意：没有id的setter
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getNoa() {
		return noa;
	}

	public void setNoa(int noa) {
		this.noa = noa;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

}
