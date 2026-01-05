package com.mojang.realmsclient.gui.screens.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsConfirmScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
class RealmsPlayersTab extends GridLayoutTab implements RealmsConfigurationTab {
    static final Logger LOGGER = LogUtils.getLogger();
    static final Component TITLE = Component.translatable("mco.configure.world.players.title");
    static final Component QUESTION_TITLE = Component.translatable("mco.question");
    private static final int PADDING = 8;
    final RealmsConfigureWorldScreen configurationScreen;
    final Minecraft minecraft;
    final Font font;
    RealmsServer serverData;
    final RealmsPlayersTab.InvitedObjectSelectionList invitedList;

    RealmsPlayersTab(RealmsConfigureWorldScreen p_410606_, Minecraft p_409396_, RealmsServer p_410131_) {
        super(TITLE);
        this.configurationScreen = p_410606_;
        this.minecraft = p_409396_;
        this.font = p_410606_.getFont();
        this.serverData = p_410131_;
        GridLayout.RowHelper gridlayout$rowhelper = this.layout.spacing(8).createRowHelper(1);
        this.invitedList = gridlayout$rowhelper.addChild(
            new RealmsPlayersTab.InvitedObjectSelectionList(p_410606_.width, this.calculateListHeight()), LayoutSettings.defaults().alignVerticallyTop().alignHorizontallyCenter()
        );
        gridlayout$rowhelper.addChild(
            Button.builder(
                    Component.translatable("mco.configure.world.buttons.invite"), p_405944_ -> p_409396_.setScreen(new RealmsInviteScreen(p_410606_, p_410131_))
                )
                .build(),
            LayoutSettings.defaults().alignVerticallyBottom().alignHorizontallyCenter()
        );
        this.updateData(p_410131_);
    }

    public int calculateListHeight() {
        return this.configurationScreen.getContentHeight() - 20 - 16;
    }

    @Override
    public void doLayout(ScreenRectangle p_409000_) {
        this.invitedList.updateSizeAndPosition(this.configurationScreen.width, this.calculateListHeight(), this.configurationScreen.layout.getHeaderHeight());
        super.doLayout(p_409000_);
    }

    @Override
    public void updateData(RealmsServer p_408665_) {
        this.serverData = p_408665_;
        this.invitedList.updateList(p_408665_);
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class Entry extends ContainerObjectSelectionList.Entry<RealmsPlayersTab.Entry> {
    }

    @OnlyIn(Dist.CLIENT)
    class HeaderEntry extends RealmsPlayersTab.Entry {
        private final Font font;
        private String cachedNumberOfInvites = "";
        private final FocusableTextWidget invitedWidget;

        public HeaderEntry(final Font p_426582_) {
            this.font = p_426582_;
            this.invitedWidget = new FocusableTextWidget(
                RealmsPlayersTab.this.invitedList.getRowWidth(),
                Component.translatable("mco.configure.world.invited.number", "").withStyle(ChatFormatting.UNDERLINE),
                p_426582_,
                false,
                FocusableTextWidget.BackgroundFill.ON_FOCUS,
                4
            );
        }

        @Override
        public void renderContent(GuiGraphics p_426376_, int p_425457_, int p_426259_, boolean p_429575_, float p_428802_) {
            String s = RealmsPlayersTab.this.serverData.players != null ? Integer.toString(RealmsPlayersTab.this.serverData.players.size()) : "0";
            if (!s.equals(this.cachedNumberOfInvites)) {
                this.cachedNumberOfInvites = s;
                MutableComponent mutablecomponent = Component.translatable("mco.configure.world.invited.number", s);
                this.invitedWidget.setMessage(mutablecomponent.withStyle(ChatFormatting.UNDERLINE));
            }

            this.invitedWidget
                .setPosition(
                    this.getX() + this.getWidth() / 2 - this.font.width(this.invitedWidget.getMessage()) / 2,
                    this.getY() + this.getHeight() / 2 - 9 / 2
                );
            this.invitedWidget.render(p_426376_, p_425457_, p_426259_, p_428802_);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.invitedWidget);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.invitedWidget);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class InvitedObjectSelectionList extends ContainerObjectSelectionList<RealmsPlayersTab.Entry> {
        private static final int PLAYER_ENTRY_HEIGHT = 36;

        public InvitedObjectSelectionList(final int p_406778_, final int p_406806_) {
            super(Minecraft.getInstance(), p_406778_, p_406806_, RealmsPlayersTab.this.configurationScreen.getHeaderHeight(), 36);
        }

        void updateList(RealmsServer p_424448_) {
            this.clearEntries();
            this.populateList(p_424448_);
        }

        private void populateList(RealmsServer p_427000_) {
            this.addEntry(RealmsPlayersTab.this.new HeaderEntry(RealmsPlayersTab.this.font), (int)(9.0F * 1.5F));

            for (RealmsPlayersTab.PlayerEntry realmsplayerstab$playerentry : p_427000_.players
                .stream()
                .map(p_430179_ -> RealmsPlayersTab.this.new PlayerEntry(p_430179_))
                .toList()) {
                this.addEntry(realmsplayerstab$playerentry);
            }
        }

        @Override
        protected void renderListBackground(GuiGraphics p_407111_) {
        }

        @Override
        protected void renderListSeparators(GuiGraphics p_410636_) {
        }

        @Override
        public int getRowWidth() {
            return 300;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class PlayerEntry extends RealmsPlayersTab.Entry {
        protected static final int SKIN_FACE_SIZE = 32;
        private static final Component NORMAL_USER_TEXT = Component.translatable("mco.configure.world.invites.normal.tooltip");
        private static final Component OP_TEXT = Component.translatable("mco.configure.world.invites.ops.tooltip");
        private static final Component REMOVE_TEXT = Component.translatable("mco.configure.world.invites.remove.tooltip");
        private static final ResourceLocation MAKE_OP_SPRITE = ResourceLocation.withDefaultNamespace("player_list/make_operator");
        private static final ResourceLocation REMOVE_OP_SPRITE = ResourceLocation.withDefaultNamespace("player_list/remove_operator");
        private static final ResourceLocation REMOVE_PLAYER_SPRITE = ResourceLocation.withDefaultNamespace("player_list/remove_player");
        private static final int ICON_WIDTH = 8;
        private static final int ICON_HEIGHT = 7;
        private final PlayerInfo playerInfo;
        private final Button removeButton;
        private final Button makeOpButton;
        private final Button removeOpButton;

        public PlayerEntry(final PlayerInfo p_431043_) {
            this.playerInfo = p_431043_;
            int i = RealmsPlayersTab.this.serverData.players.indexOf(this.playerInfo);
            this.makeOpButton = SpriteIconButton.builder(NORMAL_USER_TEXT, p_429748_ -> this.op(i), false)
                .sprite(MAKE_OP_SPRITE, 8, 7)
                .width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width(NORMAL_USER_TEXT))
                .narration(
                    p_430054_ -> CommonComponents.joinForNarration(
                        Component.translatable("mco.invited.player.narration", p_431043_.getName()),
                        p_430054_.get(),
                        Component.translatable("narration.cycle_button.usage.focused", OP_TEXT)
                    )
                )
                .build();
            this.removeOpButton = SpriteIconButton.builder(OP_TEXT, p_427656_ -> this.deop(i), false)
                .sprite(REMOVE_OP_SPRITE, 8, 7)
                .width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width(OP_TEXT))
                .narration(
                    p_429284_ -> CommonComponents.joinForNarration(
                        Component.translatable("mco.invited.player.narration", p_431043_.getName()),
                        p_429284_.get(),
                        Component.translatable("narration.cycle_button.usage.focused", NORMAL_USER_TEXT)
                    )
                )
                .build();
            this.removeButton = SpriteIconButton.builder(REMOVE_TEXT, p_430622_ -> this.uninvite(i), false)
                .sprite(REMOVE_PLAYER_SPRITE, 8, 7)
                .width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width(REMOVE_TEXT))
                .narration(p_425467_ -> CommonComponents.joinForNarration(Component.translatable("mco.invited.player.narration", p_431043_.getName()), p_425467_.get()))
                .build();
            this.updateOpButtons();
        }

        private void op(int p_422863_) {
            UUID uuid = RealmsPlayersTab.this.serverData.players.get(p_422863_).getUuid();
            RealmsUtil.<Ops>supplyAsync(
                    p_428667_ -> p_428667_.op(RealmsPlayersTab.this.serverData.id, uuid),
                    p_423803_ -> RealmsPlayersTab.LOGGER.error("Couldn't op the user", (Throwable)p_423803_)
                )
                .thenAcceptAsync(p_428958_ -> {
                    this.updateOps(p_428958_);
                    this.updateOpButtons();
                    this.setFocused(this.removeOpButton);
                }, RealmsPlayersTab.this.minecraft);
        }

        private void deop(int p_425470_) {
            UUID uuid = RealmsPlayersTab.this.serverData.players.get(p_425470_).getUuid();
            RealmsUtil.<Ops>supplyAsync(
                    p_430875_ -> p_430875_.deop(RealmsPlayersTab.this.serverData.id, uuid),
                    p_426816_ -> RealmsPlayersTab.LOGGER.error("Couldn't deop the user", (Throwable)p_426816_)
                )
                .thenAcceptAsync(p_426906_ -> {
                    this.updateOps(p_426906_);
                    this.updateOpButtons();
                    this.setFocused(this.makeOpButton);
                }, RealmsPlayersTab.this.minecraft);
        }

        private void uninvite(int p_428406_) {
            if (p_428406_ >= 0 && p_428406_ < RealmsPlayersTab.this.serverData.players.size()) {
                PlayerInfo playerinfo = RealmsPlayersTab.this.serverData.players.get(p_428406_);
                RealmsConfirmScreen realmsconfirmscreen = new RealmsConfirmScreen(
                    p_424369_ -> {
                        if (p_424369_) {
                            RealmsUtil.runAsync(
                                p_423881_ -> p_423881_.uninvite(RealmsPlayersTab.this.serverData.id, playerinfo.getUuid()),
                                p_429885_ -> RealmsPlayersTab.LOGGER.error("Couldn't uninvite user", (Throwable)p_429885_)
                            );
                            RealmsPlayersTab.this.serverData.players.remove(p_428406_);
                            RealmsPlayersTab.this.updateData(RealmsPlayersTab.this.serverData);
                        }

                        RealmsPlayersTab.this.minecraft.setScreen(RealmsPlayersTab.this.configurationScreen);
                    },
                    RealmsPlayersTab.QUESTION_TITLE,
                    Component.translatable("mco.configure.world.uninvite.player", playerinfo.getName())
                );
                RealmsPlayersTab.this.minecraft.setScreen(realmsconfirmscreen);
            }
        }

        private void updateOps(Ops p_426074_) {
            for (PlayerInfo playerinfo : RealmsPlayersTab.this.serverData.players) {
                playerinfo.setOperator(p_426074_.ops.contains(playerinfo.getName()));
            }
        }

        private void updateOpButtons() {
            this.makeOpButton.visible = !this.playerInfo.isOperator();
            this.removeOpButton.visible = !this.makeOpButton.visible;
        }

        private Button activeOpButton() {
            return this.makeOpButton.visible ? this.makeOpButton : this.removeOpButton;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.activeOpButton(), this.removeButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.activeOpButton(), this.removeButton);
        }

        @Override
        public void renderContent(GuiGraphics p_429316_, int p_429337_, int p_429057_, boolean p_423038_, float p_425807_) {
            int i;
            if (!this.playerInfo.getAccepted()) {
                i = -6250336;
            } else if (this.playerInfo.getOnline()) {
                i = -16711936;
            } else {
                i = -1;
            }

            int j = this.getContentYMiddle() - 16;
            RealmsUtil.renderPlayerFace(p_429316_, this.getContentX(), j, 32, this.playerInfo.getUuid());
            int k = this.getContentYMiddle() - 9 / 2;
            p_429316_.drawString(RealmsPlayersTab.this.font, this.playerInfo.getName(), this.getContentX() + 8 + 32, k, i);
            int l = this.getContentYMiddle() - 10;
            int i1 = this.getContentRight() - this.removeButton.getWidth();
            this.removeButton.setPosition(i1, l);
            this.removeButton.render(p_429316_, p_429337_, p_429057_, p_425807_);
            int j1 = i1 - this.activeOpButton().getWidth() - 8;
            this.makeOpButton.setPosition(j1, l);
            this.makeOpButton.render(p_429316_, p_429337_, p_429057_, p_425807_);
            this.removeOpButton.setPosition(j1, l);
            this.removeOpButton.render(p_429316_, p_429337_, p_429057_, p_425807_);
        }
    }
}