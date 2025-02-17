package fr.diamodaria.ui.panels;

import com.sun.management.OperatingSystemMXBean;
import fr.diamodaria.Controller;
import fr.diamodaria.game.Setup;
import fr.diamodaria.ui.frames.AdditionalModList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Launch extends JPanel implements ActionListener, ChangeListener
{
	private final Controller   ctrl;
	private final JButton      btnDisconnect;
	private final JButton      btnPlay;
	private final JButton      btnAdditional;
	private final JButton      btnImport;
	private final JSpinner     ramSelector;
	private final JProgressBar progressBar;
	private final JLabel       lblRamInfo;
	private final JPanel       panelBot;
	private final Image        imgFond;
	private Image              imgHead;

	public Launch( Controller ctrl )
	{
		this.imgFond = getToolkit().getImage ( getClass().getResource("/background.png") );
		this.ctrl    = ctrl;

		this.setLayout( new BorderLayout() );

		SpinnerModel ramModel = new SpinnerNumberModel(4096, 1024, 16384, 256);

		long memorySize = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize();

		this.btnDisconnect = new JButton("Déconnexion");
		this.btnPlay       = new JButton("Jouer");
		this.btnAdditional = new JButton("Mods supplémentaires");
		this.btnImport     = new JButton("Importer un profile");
		this.ramSelector   = new JSpinner(ramModel);
		this.progressBar   = new JProgressBar(0, 10000);
		this.lblRamInfo    = new JLabel(String.format(" Mémoire RAM disponible : %,d Mo ", memorySize / ( 1_024 * 1_024 )));

		this.progressBar.setStringPainted(true);
		this.progressBar.setValue(0);

		this.btnPlay      .addActionListener( this );
		this.btnDisconnect.addActionListener( this );
		this.btnAdditional.addActionListener( this );
		this.btnImport    .addActionListener( this );

		this.ramSelector.setEditor(new JSpinner.NumberEditor(this.ramSelector, "# Mo"));
		this.ramSelector.addChangeListener(this);

		Border border = BorderFactory.createLineBorder(Color.darkGray, 2);

		this.lblRamInfo.setBorder(border);
		this.lblRamInfo.setBackground( Color.lightGray );
		this.lblRamInfo.setOpaque(true);

		String ramValue = ctrl.getSaver().get("ram");
		if (ramValue != null && ramValue.matches("\\d+")) this.ramSelector.setValue( Integer.parseInt(ramValue) );

		JPanel panelTop = new JPanel();
		this.panelBot = new JPanel();

		panelTop.setOpaque( false );
		this.panelBot.setOpaque( false );

		panelTop.add( this.btnAdditional );
		panelTop.add( this.btnDisconnect );
		panelTop.add( this.btnPlay       );
		panelTop.add( this.btnImport );

		this.panelBot.add(this.ramSelector);
		this.panelBot.add(this.lblRamInfo);

		this.add(panelTop, BorderLayout.NORTH );
		this.add( this.panelBot, BorderLayout.SOUTH );
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		g.drawImage ( this.imgFond, 0, 0, this );

		if (this.imgHead != null) g.drawImage ( this.imgHead, 50, 200, this );
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ( e.getSource() == this.btnPlay )
		{
			this.ctrl.getLogger().info("Preparing launch !");

			new Thread(() ->  Setup.setup( this.ctrl, this ) ).start();
		}
		else if ( e.getSource() == this.btnAdditional )
		{
			new AdditionalModList( this.ctrl );
		}
		else if ( e.getSource() == this.btnDisconnect )
		{
			this.ctrl.getSaver().remove("msAccessToken");
			this.ctrl.getSaver().remove("msRefreshToken");
			this.ctrl.getSaver().save();

			this.ctrl.switchLogin();
		}
		else if ( e.getSource() == this.btnImport )
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setDialogTitle("Sélectionner un dossier d'instance curseforge");

			String userHome = System.getProperty("user.home");
			File defaultFolder = new File(userHome, "curseforge\\minecraft\\Instances");

			if (defaultFolder.exists() && defaultFolder.isDirectory()) {
				fileChooser.setCurrentDirectory(defaultFolder);
			}

			int result = fileChooser.showOpenDialog(null);

			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFolder = fileChooser.getSelectedFile();
				Path destinationFolder = this.ctrl.getLauncherDir();
				String[] filesToTransfer = {"servers.dat", "options.txt", "journeymap/", "schematics/", "saves/"};


				for (String item : filesToTransfer) {
					File sourceFile = new File(selectedFolder, item);
					Path destinationPath = destinationFolder.resolve(item);

					try {
						if (sourceFile.exists()) {
							if (sourceFile.isFile()) {
								Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
							} else if (sourceFile.isDirectory()) {
								copyDirectory(sourceFile.toPath(), destinationPath);
							}
						}
					} catch (IOException ee) {
						ee.printStackTrace();
					}
				}
			}
		}
	}

	public void setInLoading( boolean in )
	{
		this.panelBot.removeAll();

		if (in)
		{
			//this.panelBot.add(this.ramSelector);
			this.panelBot.add(this.progressBar);

			this.btnPlay      .setEnabled( false );
			this.btnImport    .setEnabled( false );
			this.btnAdditional.setEnabled( false );
			this.btnDisconnect.setEnabled( false );
		}
		else
		{
			this.panelBot.add(this.ramSelector);
			this.panelBot.add(this.lblRamInfo);
			this.progressBar.setValue(0);

			this.btnPlay      .setEnabled( true );
			this.btnImport    .setEnabled( true );
			this.btnAdditional.setEnabled( true );
			this.btnDisconnect.setEnabled( true );
		}

		this.repaint();
		this.revalidate();
	}

	public void setLoadingProgress( int progress )
	{
		this.progressBar.setValue( progress );
	}

	public void refreshHeadImg()
	{
		String name = this.ctrl.getAuth().getAuthInfos().getUsername();

		try
		{
			URI url = new URI(String.format("https://mc-heads.net/avatar/%s/100.png", name));

			this.ctrl.getLogger().info(url.toURL().toString());
			this.imgHead = new ImageIcon(url.toURL()).getImage();
		}
		catch (URISyntaxException | MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == this.ramSelector)
		{
			int value = (int) this.ramSelector.getValue();

			this.ctrl.getSaver().set("ram", String.valueOf(value));
			this.ctrl.getSaver().save();
		}
	}

	private static void copyDirectory(Path sourceDir, Path destinationDir) throws IOException {
		Files.walk(sourceDir).forEach(sourcePath -> {
			try {
				Path targetPath = destinationDir.resolve(sourceDir.relativize(sourcePath));
				if (Files.isDirectory(sourcePath)) {
					Files.createDirectories(targetPath);
				} else {
					Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
