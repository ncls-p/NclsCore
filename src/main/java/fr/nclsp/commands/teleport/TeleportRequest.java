package fr.nclsp.commands.teleport;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nclsp.Main;
import fr.nclsp.utils.timer.CountdownTimer;

import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("NullableProblems")
public class TeleportRequest implements CommandExecutor {
	private final Main main;

	public TeleportRequest(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (sender.hasPermission("nclsp.teleportRequest.tpa")) // check perm
			{
				if (label.equalsIgnoreCase("tpa")) {
					if (args.length != 0) {
						String targetName = args[0];
						if (Bukkit.getServer().getPlayerExact(targetName) != null) {
							Player target = Bukkit.getServer().getPlayerExact(targetName);
							if (p != target) {
								// Check if someone already ask for teleport the targeted player
								if (!(main.hMTpAskedAsker.containsKey(target))) {
									// Check if the sender already have a request pending
									if (!(main.hMTpAskedAsker.containsValue(p))) {
										// get the time of the countdown in seconds
										int time = main.getConfig().getInt(
												"config.parameters.tpa.timeToRespond");
										// send messages to the sender & the target before the timer start
										Runnable beforeTimer = () -> {
											Objects.requireNonNull(target)
													.sendMessage(Objects
															.requireNonNull(
																	main.getConfig().getString(
																			"messages.tpa.targetGetRequest"))
															.replace('&', '§')
															.replace("%sender%",
																	p.getDisplayName())
															.replace("%time%",
																	time + ""));
											p.sendMessage(Objects
													.requireNonNull(main
															.getConfig()
															.getString("messages.tpa.senderSentRequest"))
													.replace('&', '§')
													.replace("%target%",
															target.getDisplayName())
													.replace("%time%",
															time + ""));
										};
										Runnable afterTimer = () -> {
											if (main.hMTpAskedAsker
													.containsKey(target)) {
												// Remove players from the HM If the target did not respond at the end
												// of the timer
												main.hMTpAskedAsker
														.remove(target, main.hMTpAskedAsker
																.get(target));
												p.sendMessage(Objects
														.requireNonNull(main
																.getConfig()
																.getString("messages.tpa.targetDidNotRespond"))
														.replace('&', '§')
														.replace("%target%",
																Objects.requireNonNull(
																		target)
																		.getDisplayName()));
												target.sendMessage(
														Objects
																.requireNonNull(main
																		.getConfig()
																		.getString("messages.tpa.youDidNotRespond"))
																.replace('&', '§')
																.replace("%sender%",
																		p.getDisplayName()));
											}
										};
										Consumer<CountdownTimer> everySecond = countdownTimer -> {
											if (main.hMTpAskedAsker
													.containsKey(target)) {
												if (countdownTimer
														.getSecondsLeft() == 20
														|| countdownTimer
																.getSecondsLeft() == 15
														|| countdownTimer
																.getSecondsLeft() == 5
														|| countdownTimer
																.getSecondsLeft() == 3) {
													p.sendMessage(Objects
															.requireNonNull(main
																	.getConfig()
																	.getString("messages.tpa.senderSecondsLeft"))
															.replace('&', '§')
															.replace("%target%",
																	Objects.requireNonNull(
																			target)
																			.getDisplayName())
															.replace("%time%",
																	countdownTimer.getSecondsLeft()
																			+ ""));
													target.sendMessage(
															Objects
																	.requireNonNull(main
																			.getConfig()
																			.getString(
																					"messages.tpa.targetSecondsLeft"))
																	.replace('&', '§')
																	.replace("%sender%",
																			p.getDisplayName())
																	.replace("%time%",
																			countdownTimer.getSecondsLeft()
																					+ ""));
												}
											}
										};
										CountdownTimer timer = new CountdownTimer(
												main, time, beforeTimer,
												afterTimer,
												everySecond);
										timer.scheduleTimer();
										main.hMTpAskedAsker.put(target, p);
									} else // SENDER ALREADY HAVE A REQUEST PENDING
										sender.sendMessage(
												Objects.requireNonNull(
														main.getConfig().getString(
																"messages.prefix"))
														.replace('&', '§')
														+ Objects
																.requireNonNull(main
																		.getConfig()
																		.getString(
																				"messages.tpa.senderAlreadyHaveARequest"))
																.replace('&', '§'));
								} else // TARGET ALREADY HAVE A REQUEST PENDING
									sender.sendMessage(
											Objects.requireNonNull(main
													.getConfig()
													.getString("messages.prefix"))
													.replace('&', '§')
													+ Objects
															.requireNonNull(main
																	.getConfig()
																	.getString(
																			"messages.tpa.targetAlreadyHaveARequest"))
															.replace('&', '§'));
							} else
								sender.sendMessage(Objects.requireNonNull(
										main.getConfig().getString(
												"messages.prefix"))
										.replace('&', '§')
										+ Objects
												.requireNonNull(
														main.getConfig().getString(
																"messages.tpa.cantAskYourself"))
												.replace('&', '§'));
						} else // PLAYER NOT ONLINE
							sender.sendMessage(Objects
									.requireNonNull(main.getConfig()
											.getString("messages.prefix"))
									.replace('&', '§')
									+ Objects.requireNonNull(main.getConfig()
											.getString("messages.tpa.notOnline"))
											.replace('&', '§'));
					} else // NO ARGS
						sender.sendMessage(
								Objects.requireNonNull(main.getConfig()
										.getString("messages.prefix"))
										.replace('&', '§')
										+ Objects.requireNonNull(main
												.getConfig()
												.getString("messages.tpa.noArgs"))
												.replace('&', '§'));
				}
				if (label.equalsIgnoreCase("tpyes")) {
					if (main.hMTpAskedAsker.containsKey(p)) {
						Player target = main.hMTpAskedAsker.get(p);
						int time = main.getConfig()
								.getInt("config.parameters.tpa.timeBeforeTeleport");
						Runnable beforeTimer = () -> { // Remove players from the HashMap and
														// send a message to tell
														// that the request have been accepted
							p.sendMessage(
									Objects.requireNonNull(main.getConfig()
											.getString("messages.tpyes.youAccepted"))
											.replace('&', '§')
											.replace("%target%", target
													.getDisplayName())
											.replace("%time%", time + ""));
							target.sendMessage(
									Objects.requireNonNull(main.getConfig()
											.getString("messages.tpyes.requestAccepted"))
											.replace('&', '§')
											.replace("%player%", p
													.getDisplayName())
											.replace("%time%", time + ""));
							main.hMTpAskedAsker.remove(p, target);
						};
						Runnable afterTimer = () -> {
							target.teleport(p);
							target.sendMessage(Objects
									.requireNonNull(main.getConfig().getString(
											"messages.tpyes.youHaveBeenTeleported"))
									.replace('&', '§')
									.replace("%player%", p.getDisplayName()));
							p.sendMessage(Objects
									.requireNonNull(main.getConfig().getString(
											"messages.tpyes.heHaveBeenTeleported"))
									.replace('&', '§')
									.replace("%target%", target.getDisplayName()));
						};
						Consumer<CountdownTimer> everySecond = countdownTimer -> {
							if (countdownTimer.getSecondsLeft() == 10
									|| countdownTimer.getSecondsLeft() == 5
									|| countdownTimer.getSecondsLeft() == 4
									|| countdownTimer.getSecondsLeft() == 3
									|| countdownTimer.getSecondsLeft() == 2
									|| countdownTimer.getSecondsLeft() == 1) {
								target.sendMessage(Objects
										.requireNonNull(main.getConfig()
												.getString("messages.tpyes.teleportationIn"))
										.replace('&', '§')
										.replace("%time%", countdownTimer
												.getSecondsLeft()
												+ ""));
							}
						};
						CountdownTimer timer = new CountdownTimer(main, time, beforeTimer,
								afterTimer, everySecond);
						timer.scheduleTimer();
					} else // NO REQUEST PENDING
						p.sendMessage(Objects
								.requireNonNull(main.getConfig()
										.getString("messages.prefix"))
								.replace('&',
										'§')
								+ Objects.requireNonNull(main.getConfig().getString(
										"messages.tpyes.noRequestPending"))
										.replace('&', '§'));
				}
				if (label.equalsIgnoreCase("tpno")) {
					if (main.hMTpAskedAsker.containsKey(p)) {
						Player target = main.hMTpAskedAsker.get(p);
						// Remove players from the HashMap and send a message to tell that the
						// request
						// have been denied
						p.sendMessage(Objects
								.requireNonNull(main.getConfig()
										.getString("messages.tpno.youDenied"))
								.replace('&', '§')
								.replace("%target%", target.getDisplayName()));
						target.sendMessage(
								Objects.requireNonNull(main.getConfig().getString(
										"messages.tpno.requestDenied"))
										.replace('&', '§').replace("%player%",
												p.getDisplayName()));
						main.hMTpAskedAsker.remove(p, target);
					} else // NO REQUEST PENDING
						p.sendMessage(Objects
								.requireNonNull(main.getConfig()
										.getString("messages.prefix"))
								.replace('&',
										'§')
								+ Objects.requireNonNull(main.getConfig().getString(
										"messages.tpno.noRequestPending"))
										.replace('&', '§'));
				}
			} else // No Perm
				sender.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.prefix"))
						.replace('&',
								'§')
						+ Objects.requireNonNull(
								main.getConfig().getString("messages.tpa.noperm"))
								.replace('&', '§'));
		} else // NOT A PLAYER
			sender.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.prefix"))
					.replace('&', '§')
					+ "§cOnly Players can perform this command !");
		return false;
	}
}