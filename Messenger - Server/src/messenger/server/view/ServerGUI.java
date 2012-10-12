package messenger.server.view;

import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import messenger.server.controller.ClientHandler;
import messenger.server.controller.Server;

public class ServerGUI extends JFrame {

	private JPanel contentPane;
	private Map<Integer, String> values;
	private Integer[] userIDs;
	private JList<String> clientList;
	private JTextArea clientInfoArea;
	private JTextArea statusArea;
	
	public ServerGUI() {
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 460, 362);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);		
		
		clientList = new JList<String>();
		clientList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {
					int clientID = userIDs[clientList.getSelectedIndex()];
					ClientHandler clientHandler = Server.clientConnections.get(clientID);
					String info = new StringBuilder()
							.append("Name: " + clientHandler.getClientName())
							.append("\nID: " + clientID)
							.append("\nIP: " + clientHandler.getIP())
							.append("\nPort: " + clientHandler.getPort())
							.toString();
					clientInfoArea.setText(info);
				}
			}
		});
		JScrollPane clientPane = new JScrollPane(clientList);
		clientPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		statusArea = new JTextArea();
		statusArea.setColumns(5);
		JScrollPane statusPane = new JScrollPane(statusArea);
		statusPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		clientInfoArea = new JTextArea();
		clientInfoArea.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addComponent(statusPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addComponent(clientPane, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(clientInfoArea, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(clientInfoArea)
						.addComponent(clientPane, GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(statusPane, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	public void setListValues(final Map<Integer, String> values) {
		
		this.values = values;
		userIDs = values.keySet().toArray(new Integer[values.size()]);
		clientList.setModel(new AbstractListModel<String>() {
			
			public int getSize() {
				return values.size();
			}
			
				public String getElementAt(int index) {
					try {
						Integer userID = userIDs[index];
						return values.get(userID);
					} catch(ArrayIndexOutOfBoundsException exception) {
						return "";
					}
				}
		});

		repaint();
	}
	
	public void showMessage(String message) {
		statusArea.append(message + "\n");
	}
}
