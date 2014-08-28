import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.XMLReaderFactory;

import com.sun.org.apache.xml.internal.serializer.OutputPropertyUtils;
public class XQuery extends JFrame{

	private Document doc;
	private TableModel model;
	private JTextArea textarea;
	private JTextPane xmlTextDocument;
	private List<Element> nodes;
	private NamespaceDialog nsdialog;
	private NamespaceContext nsctx;
	private XmlEditorStyledDocument xesd;
	
	private void alertError(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String message = sw.toString();
		JScrollPane scroll = new JScrollPane(new JTextArea(message));
		scroll.setPreferredSize(new Dimension(640,480));
		
		String title = e.toString();
		JOptionPane.showMessageDialog(this, scroll, title, JOptionPane.ERROR_MESSAGE);
	}
	
	private void alertMessage(String title, String message){
		JScrollPane scroll = new JScrollPane(new JTextArea(message));
		scroll.setPreferredSize(new Dimension(640,480));
		JOptionPane.showMessageDialog(this, scroll, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public void start()throws Exception{
	
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640,480);
		
		model = new TableModel(Collections.EMPTY_LIST);

		JTable table = new JTable(model);
		table.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2) return;
				JTable table = (JTable)e.getSource();
				int row = table.getSelectedRow();
				if(row == -1)return;

				Element element = nodes.get(row);
				dumpElement(element);
			}
			
		});
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(getNorthPane(), BorderLayout.NORTH);
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(new JScrollPane(table));
		split.setBottomComponent(getSouthPane());
		panel.add(split, BorderLayout.CENTER);
		split.setDividerLocation(0.8D);
		
		xesd = new XmlEditorStyledDocument();
		xmlTextDocument = new JTextPane(xesd);
		JScrollPane xmlScroll = new JScrollPane(xmlTextDocument);

		JTabbedPane tabs = new JTabbedPane();
		Component xpathTab = tabs.add("XPath", panel);
		Component xmlTab = tabs.add("XML", xmlScroll);
		tabs.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tabs = (JTabbedPane)e.getSource();
				int index = tabs.getSelectedIndex();
				if (index != 0)return;
				boolean parsed = updateDoc();
				if(!parsed)tabs.setSelectedIndex(1);
			}
		});

		setContentPane(tabs);
		setVisible(true);

		/*
		new DefaultJDOMFactory().document((Element) doc.getDocumentElement());
		JDOMSource.// jds = new JDOMSource();// (doc.getDocumentElement());
		*/
	}

	private boolean updateDoc(){
		StringReader sr = new StringReader(xmlTextDocument.getText());

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		SAXParserFactory spFactory = SAXParserFactory.newInstance();
		SAXParser sParser = null;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(sr)); // Create from whole cloth
			sParser = spFactory.newSAXParser();

			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			
			StringWriter wr = new StringWriter(); 
			tr.transform(new DOMSource(doc), new StreamResult(wr));
			String transformed = wr.toString();
			xmlTextDocument.setText(transformed);
			sr = new StringReader(transformed);
			doc = builder.parse(new InputSource(sr)); // Create from whole cloth
			sr = new StringReader(transformed);
			sParser.parse(new InputSource(sr), new MyHandler(xesd));
			
//			XMLReader parser = XMLReaderFactory.createXMLReader();
//	        DefaultHandler handler = new MyHandler(xesd);   
//	        parser.setContentHandler(handler);
//	        //parse xml file
//	        parser.parse(new InputSource(sr));

//			namespaces  = new HashSet<Namespace>();
//			Iterator iter = jdoc.getDescendants();
//			while(iter.hasNext()){
//				Object obj = iter.next();
//				if(obj instanceof Element){
//					Namespace n = ((Element)obj).getNamespace();
//					if(n!= null)namespaces.add(n);
//				}
//			}
			return true;
		} catch (Exception e) {
			alertError(e);
			return false;
		}
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		new XQuery().start(); 
	}

	private  JPanel getNorthPane(){
		final JTextField text = new JTextField(30);
		JButton nsConf = new JButton("Namespaces");
		nsConf.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				configureNamespace();
			}
		});
		JButton query = new JButton("Esegui");
		query.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String value = text.getText();
				performQuery(value);
			}
		});
		
		JPanel ret = new JPanel();
		ret.add(text);
		ret.add(query);
		ret.add(nsConf);
		return ret;
	}
	
	private Container getSouthPane(){
		textarea = new JTextArea();
		return new JScrollPane(textarea);
	}

	private  String extractXPath(Element element){
		StringBuilder path = new StringBuilder();
		//path.append(element.getName());

		Node parent = (Node)element.getParentNode();
		while(true){
			int index = elementIndex(element);
			if(index != -1){
				path.insert(0, "["+(index+1)+"]");	
			}
			
			String localName = element.getLocalName();
			if(localName != null){
				String prefix = element.getPrefix();
				path.insert(0,localName);
				if(prefix != null){
					path.insert(0,":");
					path.insert(0,prefix);
				}
			}else{
				path.insert(0,element.getNodeName());
			}
			path.insert(0,"/");
			//path.insert(0, parent.getName());
			
			if(parent.getParentNode() == null || parent.getParentNode().getNodeType() != Node.ELEMENT_NODE)break;
			element = (Element)parent;
			parent = parent.getParentNode();
		}

		return path.toString();
	}

	private  int elementIndex(Node element){
		Node parent = element.getParentNode();
		if(parent == null)return -1;

		NodeList list = parent.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if(list.item(i)==element)return i;
		}
		return -1;
	}

	private void performQuery(String txt){
		if(doc == null){
			alertMessage("Dom null", "Verificare il documento xml");
			return;
		}
		try{
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			if(nsctx != null)xpath.setNamespaceContext(nsctx);
			javax.xml.xpath.XPathExpression expr = xpath.compile(txt);
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			List<String> xpaths = new ArrayList<String>();
			this.nodes = new ArrayList<Element>();
			for (int i = 0; i < nodes.getLength(); i++) {
			    Node node = nodes.item(i);
			    if (node instanceof Element){
			    	xpaths.add( extractXPath((Element)node) );
			    	this.nodes.add((Element)node);
			    }else{
					System.out.println(node);
				}
			}
			/*
			
			xpath.addNamespace("tpl", "http://sun.com/jccms/1.0#template");
			xpath.addNamespace("xsl", "http://www.w3.org/1999/XSL/Transform");
			xpath.addNamespace("include", "http://apache.org/cocoon/include/1.0");
			xpath.addNamespace("collection", "http://apache.org/cocoon/collection/1.0");
			xpath.addNamespace("cms", "http://sun.com/jccms/1.0#cms");
			xpath.addNamespace("pub", "http://sun.com/jccms/1.0#publishing");
			xpath.addNamespace("html", "http://www.w3.org/1999/xhtml");
			xpath.addNamespace("sql", "http://apache.org/cocoon/SQL/2.0");
			xpath.addNamespace("str", "http://xsltsl.org/string");
			*/
			
			setTitle("Trovati " + xpaths.size() +" elementi");
			model.setPaths(xpaths);

		}catch(Exception e){
			alertError(e);
		}
	}
	
	private void dumpElement(Element e){
		StringWriter sw = new StringWriter();
		XMLOutputter out = new XMLOutputter();
		try {
			out.output(new DOMBuilder().build(e), sw);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String txt = sw.toString();
		textarea.setText(txt);
	}

	private void configureNamespace(){
		if(nsdialog == null)nsdialog = new NamespaceDialog(this);
		Map<String,String>namespaces = nsdialog.getNamespaces();

		nsctx = new MapNamespaceContext(namespaces);
	}
	
	private class MapNamespaceContext implements NamespaceContext{

		private Map<String,String>map;
		public MapNamespaceContext(Map<String,String>map){
			this.map = map;
		}

		@Override
		public String getNamespaceURI(String prefix) {
			return map.get(prefix);
		}

		@Override
		public String getPrefix(String namespaceURI) {
			for (Entry<String,String> entry : map.entrySet()) {
				if(namespaceURI.equals(entry.getValue())){
					return entry.getKey();
				}
			}
			return null;
		}

		@Override
		public Iterator<String> getPrefixes(String namespaceURI) {
			return map.keySet().iterator();
		}

	}
}
