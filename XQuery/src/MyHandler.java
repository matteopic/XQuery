import javax.swing.text.BadLocationException;
import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.DefaultHandler;

public class MyHandler extends DefaultHandler {

	private Locator2 locator;
	private XmlEditorStyledDocument xesd;
	private int lastOffset;
	private String originalXml;

	public MyHandler(XmlEditorStyledDocument xesd) {
		this.xesd = xesd;
	}


	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

//		System.out.println("start element " + qName + " at: "
//				+ locator.getLineNumber() + ":" + locator.getColumnNumber());
		
		super.startElement(uri, localName, qName, attributes);

		int offsetEnd = getOffset();
//		int offsetStart = offsetEnd - 2  - qName.length();
		int offsetStart = originalXml.lastIndexOf('<', offsetEnd);
		
		
//		System.out.println("("+offsetStart+"," +offsetEnd+")" + originalXml.substring(offsetStart, offsetEnd));
		
		if(attributes.getLength() == 0){
			assert offsetEnd - offsetStart == qName.length() + 2 : "Offset errati per l'elemnto " + qName +": " + offsetStart + ", " +offsetEnd ;
		}
		
//		int offsetStart = offsetEnd - elementStartAt;
//		System.out.println("element Offset " + offsetStart);
		int length = offsetEnd - offsetStart;
		xesd.setElement(offsetStart, length);

		xesd.setText(lastOffset, offsetStart - lastOffset);
		lastOffset = offsetEnd;
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

//		System.out.println("end element " + qName + " at: "
//				+ locator.getLineNumber() + ":" + locator.getColumnNumber());
		int offsetEnd = getOffset();
		int offsetStart = originalXml.lastIndexOf('<', offsetEnd);
//		int offsetStart = offsetEnd - elementStartAt;
		int length = offsetEnd - offsetStart;
		xesd.setElement(offsetStart, length);

		xesd.setText(lastOffset, offsetStart - lastOffset);
		lastOffset = offsetEnd;
	
	}
	
	@Override
	public void startDocument() throws SAXException {
//		System.out.println("start document at: "
//				+ locator.getLineNumber() + ":" + locator.getColumnNumber());

		try {
			xesd.clear();
			originalXml = xesd.getText(0, xesd.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
//		System.out.println("start prefix mapping at: "
//				+ locator.getLineNumber() + ":" + locator.getColumnNumber());
	}
	
	@Override
	public void endDocument() throws SAXException {
//		System.out.println("end document at: "
//				+ locator.getLineNumber() + ":" + locator.getColumnNumber());

		originalXml = null;
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
//		System.out.println("end prefix mapping at: "
//				+ locator.getLineNumber() + ":" + locator.getColumnNumber());
	}
	
	public int getOffset(){
		//row starts from 1
		int row = locator.getLineNumber();
		int col = locator.getColumnNumber();

//		String EOL = System.getProperty("line.separator");

		int offset = 0;
		for(int i = 1; i < row; i++){
			int oelIndex = originalXml.indexOf('\n', offset);
			offset = oelIndex + 1;
		}
		offset += (col - 1);

//			String str = xesd.getText(0, xesd.getLength());
//
//			String[] lines = str.split(  );
//			int offset = 0;
//			int linec = 1;
//			for(String line: lines) {
//				if(linec == row) {
//					offset += col;
//					break;
//				}
//				linec++;
//				if(linec > 2)
//					offset++;
//				offset += line.length();
//			}		
			return offset;
	}
	
	

//	@Override
//	public void characters(char[] ch, int start, int length)
//			throws SAXException {
//		if(ignoreChars)return;
//
//		System.out.println("starting "+length+"  characters at: "
//				+ locator.getLineNumber() + ":" + locator.getColumnNumber());
//		super.characters(ch, start, length);
//		int offsetEnd = getOffset();
//		int offsetStart = offsetEnd - length;
////		int length = offsetEnd - offsetStart;
//		xesd.setText(offsetStart, length);
//	}

	@Override
	public void setDocumentLocator(Locator locator) {
		super.setDocumentLocator(locator);
		this.locator = (Locator2)locator;
	}
}
