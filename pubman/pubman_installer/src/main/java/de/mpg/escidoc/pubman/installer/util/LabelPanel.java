package de.mpg.escidoc.pubman.installer.util;

import java.awt.Dimension;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.IzPanel;

public class LabelPanel extends JPanel {
	
	
	private JTextArea textArea;
	private JLabel installLabel;
	private JScrollPane scrollPane;
	private JSeparator separator;
	private JLabel endLabel;
	private JProgressBar progressBar;
	
	public static ImageIcon ICON;

	public static ImageIcon ICON_SUCCESS;

	public static ImageIcon ICON_WARNING;

	public static ImageIcon ICON_ERROR;
	
	public LabelPanel(String installMessage, boolean showProgressBar)
	{
		super();
		
		try {
			ICON = new ImageIcon(ImageIO.read(IzPanel.class
					.getResourceAsStream("/img/wizard.png")));
			ICON_WARNING = new ImageIcon(ImageIO.read(IzPanel.class
					.getResourceAsStream("/img/messagebox_warning.png")));
			ICON_SUCCESS = new ImageIcon(ImageIO.read(IzPanel.class
					.getResourceAsStream("/img/check32.png")));
			ICON_ERROR = new ImageIcon(ImageIO.read(IzPanel.class
					.getResourceAsStream("/img/error.png")));
		} catch (IOException e) {
			System.out.println("Icons not found! " + e.toString());
		}
		
		BoxLayout bl = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(bl);
		this.setPreferredSize(new Dimension(700, 100));
		installLabel = LabelFactory.create(installMessage, ICON, SwingConstants.LEADING);
		installLabel.setAlignmentX(LEFT_ALIGNMENT);
		add(installLabel);
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(showProgressBar);
		progressBar.setAlignmentX(LEFT_ALIGNMENT);
		this.add(progressBar);
		
		endLabel = LabelFactory.create("", ICON, SwingConstants.LEADING);
		endLabel.setVisible(false);
		endLabel.setAlignmentX(LEFT_ALIGNMENT);
		this.add(endLabel);
		
		textArea = new JTextArea();
		scrollPane = new JScrollPane(textArea);
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		textArea.setEditable(false);
		textArea.setRows(3);
		textArea.setLineWrap(true);
		textArea.setAlignmentX(LEFT_ALIGNMENT);
		
		scrollPane.setVisible(false);
		this.add(scrollPane);
		
		separator = new JSeparator(JSeparator.HORIZONTAL);
		this.add(separator);
		
		this.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
		revalidate();	
	}
	
	
	public void setEndLabel(String message, ImageIcon icon)
	{
		endLabel.setIcon(icon);
		endLabel.setText(message);
		endLabel.setVisible(true);
		revalidate();
	}
	
	
	public void addToTextArea(String message)
	{
		textArea.append(message);
		scrollPane.setVisible(true);
		revalidate();
	}
	
	public void showProgressBar(boolean show)
	{
		progressBar.setVisible(show);
		revalidate();
	}
	

}
