import javax.swing.text.BadLocationException;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MyHandler extends DefaultHandler {

	private Locator locator;
	private XmlEditorStyledDocument xesd;
	private int lastOffset;

	public MyHandler(XmlEditorStyledDocument xesd) {
		this.xesd = xesd;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		int offsetEnd = getOffset();
		int offsetStart = offsetEnd - 2  - qName.length();
		int length = offsetEnd - offsetStart;
		xesd.setElement(offsetStart, length);

		System.out.println("start element " + qName + " at: "
				+ locator.getLineNumber() + ":" + locator.getColumnNumber());
		
		lastOffset = offsetEnd;
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		int offsetEnd = getOffset();
		int offsetStart = offsetEnd - 3  - qName.length();
		int length = offsetEnd - offsetStart;
		xesd.setElement(offsetStart, length);

		System.out.println("end element " + qName + " at: "
				+ locator.getLineNumber() + ":" + locator.getColumnNumber());
		
		lastOffset = offsetEnd;
	
	}
	
	@Override
	public void startDocument() throws SAXException {
		System.out.println("start document at: "
				+ locator.getLineNumber() + ":" + locator.getColumnNumber());
	}
	
	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
		System.out.println("start prefix mapping at: "
				+ locator.getLineNumber() + ":" + locator.getColumnNumber());
	}
	
	@Override
	public void endDocument() throws SAXException {
		System.out.println("end document at: "
				+ locator.getLineNumber() + ":" + locator.getColumnNumber());
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		System.out.println("end prefix mapping at: "
				+ locator.getLineNumber() + ":" + locator.getColumnNumber());
	}
	
	public int getOffset(){
		int row = locator.getLineNumber();
		int col = locator.getColumnNumber();

		try {
			String str = xesd.getText(0, xesd.getLength());

			String[] lines = str.split("\n");
			int offset = 0;
			int linec = 1;
			for(String line: lines) {
				if(linec == row) {
					offset += col;
					break;
				}
				linec++;
				if(linec > 2)
					offset++;
				offset += line.length();
			}		
			return offset;
		} catch (BadLocationException e) { return -1;}
	}
	
	

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);

		int offsetEnd = getOffset();
		int offsetStart = offsetEnd - length;
//		int length = offsetEnd - offsetStart;
		xesd.setText(offsetStart, length);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		super.setDocumentLocator(locator);
		this.locator = locator;
	}
}
