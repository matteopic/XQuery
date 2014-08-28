import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class NamespaceDialog extends JDialog {

	private JTextArea text;
	private Map<String,String>namespaces;

	public NamespaceDialog(JFrame owner){
		super(owner, true);
		setTitle("Configurazione namespace");
	}

	public void setNamespaces(Map<String,String> namespaces){
		this.namespaces = namespaces; 
	}

	private void initGUI(){
		if(text != null)return;
		text = new JTextArea(20,50);
		JButton ok = new JButton("OK");
		JButton annulla = new JButton("Annulla");
		JScrollPane scroll = new JScrollPane(text);
		

		getContentPane().setLayout(new FlowLayout());
		getContentPane().add(scroll);
		getContentPane().add(ok);
		getContentPane().add(annulla);

		ok.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				parseNamespaces();
			}
		});
		
		annulla.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				annulla();
			}
		});
		pack();
	}

	public Map<String, String> getNamespaces(){
		initGUI();
		if(namespaces != null){
			StringBuilder sb = new StringBuilder();
			for (Entry<String,String> entry : namespaces.entrySet()) {
				sb.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
			}
			text.setText(sb.toString());
		}
		setVisible(true);
		return namespaces;
	}

	private void parseNamespaces(){
		namespaces = new LinkedHashMap<String,String>();
		String txt = text.getText();
		StringTokenizer st = new StringTokenizer(txt, "", false);
		String prefix = null;
		String uri = null;
		while(true){
			try{
				prefix = st.nextToken(":");
				uri = st.nextToken("\n").substring(1);
				namespaces.put(prefix.trim(), uri.trim());	
			}catch(NoSuchElementException e){
				break;
			}
		}
		dispose();
	}

	private void annulla(){
		dispose();
	}
}
