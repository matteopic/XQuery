import java.awt.Color;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;


public class XmlEditorStyledDocument extends DefaultStyledDocument {
	private SimpleAttributeSet elementAttributes;
	private SimpleAttributeSet textAttributes;
	private SimpleAttributeSet defaultAttributes;
	private Style defaultStyle;

	public XmlEditorStyledDocument(){
		super();
		StyleContext sc = (StyleContext)getAttributeContext();
		defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
		
		elementAttributes = new SimpleAttributeSet();
	    StyleConstants.setForeground(elementAttributes, Color.BLUE);

	    textAttributes = new SimpleAttributeSet();
	    StyleConstants.setForeground(textAttributes, Color.BLACK);

//	    StyleConstants.setBackground(elementAttributes, Color.RED);
//	    getStyle("element").addAttributes(elementAttributes);
	}

	public void setElement(int offset, int length){
		setCharacterAttributes(offset, length, elementAttributes, true);
	}
	
	public void setText(int offset, int length){
		setCharacterAttributes(offset, length, textAttributes, true);
	}
//
//	public static void main(String[] args) {
//		XmlEditorStyledDocument xsd = new XmlEditorStyledDocument();
//
//		JFrame f = new JFrame();
//		JTextPane tp = new JTextPane(xsd);
//		f.setContentPane(new JScrollPane(tp));
//		f.setSize(new Dimension(800,600));
//		f.setVisible(true);
//		
//		tp.setText("pre  <html>  post");
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		xsd.setElement(5,0,11,0);
//	}

	public void clear() {
		setCharacterAttributes(0, getLength(), defaultStyle, true);
	}
	

}
