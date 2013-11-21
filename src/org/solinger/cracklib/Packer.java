package org.solinger.cracklib;


import java.io.IOException;
import java.io.RandomAccessFile;

public class Packer {

	public static final int MAGIC = 0x70775631;
	public static final int STRINGSIZE = 1024;
	public static final int TRUNCSTRINGSIZE = STRINGSIZE/4;
	public static final int NUMWORDS = 16;
	public static final int MAXWORDLEN = 32;
	public static final int MAXBLOCKLEN = (MAXWORDLEN * NUMWORDS);
	public static final int INTSIZ = 4;

	protected RandomAccessFile dataFile; // data file
	protected RandomAccessFile indexFile; // index file
	protected RandomAccessFile hashFile; // hash file

	protected PackerHeader header;

	protected int[] hwms = new int[256];
	protected int count;
	protected String lastWord;
	protected String[] data = new String[NUMWORDS];

	protected int block = -1;

	public Packer(String path) throws IOException {
		dataFile = new RandomAccessFile(path+".pwd","r"); // data file
		indexFile = new RandomAccessFile(path+".pwi","r"); // index file
		try {
			hashFile = new RandomAccessFile(path+".hwm","r"); // hash file
		} catch (IOException e) {
			hashFile = null; // hashFile isn't mandatory.
		}

		header = PackerHeader.parse(indexFile);
		if (header.getMagic() != MAGIC) {
			throw new IOException("Magic Number mismatch");
		} else if (header.getBlockLen() != NUMWORDS) {
			throw new IOException("Size mismatch");
		}

		// populate the hwms..
		if (hashFile != null) {
			byte[] b = new byte[4];
			for (int i=0;i<hwms.length;i++) {
				hashFile.readFully(b);
				hwms[i] = Util.getIntLE(b);
			}
		}
	}

	public synchronized void close() throws IOException {
		indexFile.close();
		dataFile.close();
		if (hashFile != null) {
			hashFile.close();
		}
	}

	public synchronized String get(int num) throws IOException {
		if (header.getNumWords() <= num) { // too big
			return null;
		}

		byte[] index = new byte[4];
		byte[] index2 = new byte[4];

		int thisblock = num / NUMWORDS;

		if (block == thisblock) {
			return (data[num % NUMWORDS]);
		}

		//System.out.println("len="+indexFile.length());
		//System.out.println("thisblock="+thisblock);

		// get the index of this block.
		indexFile.seek(PackerHeader.sizeOf() + (thisblock * INTSIZ));
		indexFile.readFully(index,0,index.length);

		byte[] buf = null;
		try {
			// get the index of the next block.
			indexFile.seek(indexFile.getFilePointer()+INTSIZ);
			indexFile.readFully(index2,0,index2.length);

			buf = new byte[Util.getIntLE(index2)-Util.getIntLE(index)];
		} catch (IOException e) { // EOF
			buf = new byte[MAXBLOCKLEN];
		}

		//System.out.println("This index="+Util.getIntLE(index));
		//System.out.println("Next index="+Util.getIntLE(index2));

		// read the data
		dataFile.seek(Util.getIntLE(index));
		Util.readFullish(dataFile,buf,0,buf.length);

		block = thisblock;

		byte[] strbuf = new byte[MAXWORDLEN];
		int a = 0;
		int off = 0;

		for (int i=0;i<NUMWORDS;i++) {
			int b = a;
			for (;buf[b] != '\0';b++) {}
			//System.out.println("a="+a+",b="+b+",off="+off);
			if (b == a) { // not more \0's
				break;
			}
			System.arraycopy(buf,a,strbuf,off,(b-a));
			data[i] = new String(strbuf,0,off+(b-a));
			//System.out.println(data[i]);
			a = b+2;
			off = buf[a-1];
		}

		return (data[num % NUMWORDS]);
	}

	public int find(String s) throws IOException {
		int index = (int) s.charAt(0);
		int lwm = index != 0 ? hwms[index - 1] : 0;
		int hwm = hwms[index];


		for (;;) {
			int middle = lwm + ((hwm - lwm + 1) / 2);

			if (middle == hwm) {
				break;
			}

			int cmp = s.compareTo(get(middle));

			if (cmp < 0) {
				hwm = middle;
			} else if (cmp > 0) {
				lwm = middle;
			} else {
				return middle;
			}
		}
		return -1;
	}

	public int size() {
		return header.getNumWords();
	}
}
