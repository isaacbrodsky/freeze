/**
 * 
 */
package com.isaacbrodsky.freeze.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Provides a (very) simple multi line modal input dialog box.
 * 
 * <p>
 * from <a href=
 * "http://www.java-forums.org/awt-swing/542-multiple-line-input-dialog-box.html"
 * >http://www.java-forums.org/awt-swing/542-multiple-line-input-dialog-box
 * .html</a>
 * 
 * @author isaac
 * 
 */
public class MutliLineInputDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6453314120736535789L;

	JTextArea ta = new JTextArea(25, 50);
	JButton okButton = new JButton("   OK   ");
	JButton cancelButton = new JButton("Cancel");
	String str = null;

	/**
	 */
	public MutliLineInputDialog(Frame parent, String title, String message) {
		super(parent, title);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		if (parent != null)
			setLocation(parent.getWidth() / 2, parent.getHeight() / 2);
		else
			setLocation(320, 200);
		getContentPane().add(new JLabel(message), BorderLayout.NORTH);
		getContentPane().add(ta, BorderLayout.CENTER);
		JPanel jp = new JPanel();
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		jp.add(okButton);
		jp.add(cancelButton);
		getContentPane().add(jp, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == okButton)
			str = ta.getText();
		dispose();
	}

	/**
	 * @return
	 */
	public String getData() {
		return str;
	}

	/**
	 * Returns <code>null</code> if cancel is selected, else the text entered by
	 * the user.
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @return
	 */
	public static String showInputDialog(Frame parent, String title,
			String message) {
		MutliLineInputDialog mlid = new MutliLineInputDialog(parent, title,
				message);
		return mlid.getData();
	}

}
