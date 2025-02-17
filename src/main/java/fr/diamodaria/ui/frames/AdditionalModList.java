package fr.diamodaria.ui.frames;

import fr.diamodaria.Controller;
import fr.theshark34.openlauncherlib.util.Saver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdditionalModList extends JFrame implements ActionListener {
	private final JTextArea  txtArea;
	private final Controller ctrl;

	public AdditionalModList(Controller ctrl)
	{
		this.ctrl = ctrl;

		this.setTitle("Mods supplémentaires");
		this.setLocationRelativeTo(null);
		this.setSize(450, 350);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel p = new JPanel( new BorderLayout(20, 20) );

		this.txtArea = new JTextArea();
		JButton btn = new JButton("Valider");
		btn.addActionListener(this);

		Saver s = this.ctrl.getSaver();

		this.txtArea.setText( s.get("additional_mod_list", "") );

		p.add(btn,     BorderLayout.SOUTH  );
		p.add( this.txtArea, BorderLayout.CENTER );

		this.add(p);

		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Saver s = this.ctrl.getSaver();

		s.set("additional_mod_list", this.txtArea.getText());
		s.save();

		this.dispose();
	}
}