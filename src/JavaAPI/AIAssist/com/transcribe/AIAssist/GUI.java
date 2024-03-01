package com.transcribe.AIAssist;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Color;

public class GUI {
	static String text;
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(128, 128, 128));
		frame.setBounds(100, 100, 510, 384);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);

		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 39, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(scrollPane);

		JToggleButton tglbtnNewToggleButton = new JToggleButton("Record");
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -2, SpringLayout.NORTH, tglbtnNewToggleButton);

		JTextArea textArea = new JTextArea(
				"Make a selection above, then click record. Click record again when you are done speaking.");
		textArea.setForeground(new Color(239, 239, 239));
		textArea.setBackground(new Color(128, 128, 128));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		scrollPane.setViewportView(textArea);
		springLayout.putConstraint(SpringLayout.WEST, tglbtnNewToggleButton, 89, SpringLayout.WEST,
				frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, tglbtnNewToggleButton, -99, SpringLayout.EAST,
				frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tglbtnNewToggleButton, 0, SpringLayout.SOUTH,
				frame.getContentPane());
		tglbtnNewToggleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tglbtnNewToggleButton.isSelected()) {
					try {
						GUI_BackEnd.setFileLocation(Audio_IO.startRecording());
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				} else {
					Audio_IO.stopRecording();
					if (!tglbtnNewToggleButton.isSelected()) {
						if (GUI_BackEnd.getSelection() == 0)
							try {
								GUI_BackEnd.Question();
								if(!GUI_BackEnd.onHold(GUI_BackEnd.getAnswer()))
									textArea.append("\nAI Response: "+GUI_BackEnd.getAnswer());
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						else
							try {
								setText(GUI_BackEnd.Translation());
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

					}
				}
			}

		});
		frame.getContentPane().add(tglbtnNewToggleButton);

		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setForeground(new Color(221, 221, 221));
		comboBox.setBackground(new Color(5, 17, 46));
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Questions", "Translations" }));
		springLayout.putConstraint(SpringLayout.WEST, comboBox, 117, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, comboBox, -6, SpringLayout.NORTH, scrollPane);
		springLayout.putConstraint(SpringLayout.EAST, comboBox, 308, SpringLayout.WEST, frame.getContentPane());

		frame.getContentPane().add(comboBox);

		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedOption = comboBox.getSelectedItem().toString();
				if (selectedOption.equals("Questions")) {
					GUI_BackEnd.setSelection(0);
				} else if (selectedOption.equals("Translations")) {
					GUI_BackEnd.setSelection(1);

				}

			}
		});
	}

	public static String getText() {
		return text;
	}

	public static void setText(String text) {
		GUI.text = text;
	}

}