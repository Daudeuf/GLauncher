package fr.glauncher.game;

import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.flowarg.flowupdater.download.json.CurseModPackInfo;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.AbstractForgeVersion;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.glauncher.Controller;
import fr.glauncher.ui.panels.Launch;

import java.nio.file.Path;

public class Setup
{
	public static void setup(Controller ctrl, Launch launch)
	{
		IProgressCallback callback = makeCallback( ctrl, launch );

		try
		{
			Controller.pack.refresh();

			final VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
					.withName(Controller.pack.getVersion().split("-")[0])
					.build();

			final AbstractForgeVersion forge = new ForgeVersionBuilder(ForgeVersionType.NEW)
					.withForgeVersion(Controller.pack.getVersion())
					.withCurseModPack(new CurseModPackInfo(
							Controller.pack.getProjectId(),
							Controller.pack.getFileId(),
							true)
					)
					.withFileDeleter(new ModFileDeleter(true))
					.build();

			final FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
					.withVanillaVersion(vanillaVersion)
					.withModLoaderVersion(forge)
					.withLogger(ctrl.getLogger())
					.withProgressCallback(callback)
					.build();

			updater.update(ctrl.getLauncherDir());

			ctrl.getLogger().info("Lancement !");
			Start.Start(ctrl, updater.getVanillaVersion().getName());
		}
		catch (Exception exp)
		{
			ctrl.getLogger().printStackTrace(exp);
		}
	}

	public static IProgressCallback makeCallback(Controller ctrl, Launch launch)
	{
		return new IProgressCallback()
		{
			@Override
			public void step(Step step)
			{
				launch.setInLoading( true );

				if (step.name().equals("END"))
				{
					launch.setInLoading( false );
					ctrl.hide();
				}
			}

			@Override
			public void update(DownloadList.DownloadInfo info)
			{
				launch.setLoadingProgress( (int) (10000.0 * info.getDownloadedBytes() / info.getTotalToDownloadBytes()) );
			}

			@Override
			public void onFileDownloaded(Path path)
			{
				// ctrl.getLogger().info(path.toString());
			}
		};
	}
}
