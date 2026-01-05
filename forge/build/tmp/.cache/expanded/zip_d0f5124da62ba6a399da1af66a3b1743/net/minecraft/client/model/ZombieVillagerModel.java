package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.ZombieVillagerRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieVillagerModel<S extends ZombieVillagerRenderState> extends HumanoidModel<S> implements VillagerLikeModel<S> {
    public ZombieVillagerModel(ModelPart p_171092_) {
        super(p_171092_);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild(
            "head",
            new CubeListBuilder()
                .texOffs(0, 0)
                .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F)
                .texOffs(24, 0)
                .addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F),
            PartPose.ZERO
        );
        PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild(
            "hat",
            CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F)),
            PartPose.ZERO
        );
        partdefinition2.addOrReplaceChild(
            "hat_rim",
            CubeListBuilder.create().texOffs(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F),
            PartPose.rotation((float) (-Math.PI / 2), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
                .texOffs(16, 20)
                .addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
                .texOffs(0, 38)
                .addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.05F)),
            PartPose.ZERO
        );
        partdefinition.addOrReplaceChild(
            "right_arm", CubeListBuilder.create().texOffs(44, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-5.0F, 2.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(44, 22).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
            PartPose.offset(5.0F, 2.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-2.0F, 12.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
            PartPose.offset(2.0F, 12.0F, 0.0F)
        );
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createNoHatLayer() {
        return createBodyLayer().apply(p_427864_ -> {
            p_427864_.getRoot().clearChild("head").clearRecursively();
            return p_427864_;
        });
    }

    public static ArmorModelSet<LayerDefinition> createArmorLayerSet(CubeDeformation p_430714_, CubeDeformation p_423370_) {
        return createArmorMeshSet(ZombieVillagerModel::createBaseArmorMesh, p_430714_, p_423370_).map(p_425260_ -> LayerDefinition.create(p_425260_, 64, 32));
    }

    private static MeshDefinition createBaseArmorMesh(CubeDeformation p_431700_) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(p_431700_, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild(
            "head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_431700_), PartPose.ZERO
        );
        partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_431700_.extend(0.1F)),
            PartPose.ZERO
        );
        partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_431700_.extend(0.1F)),
            PartPose.offset(-2.0F, 12.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_431700_.extend(0.1F)),
            PartPose.offset(2.0F, 12.0F, 0.0F)
        );
        partdefinition1.getChild("hat").addOrReplaceChild("hat_rim", CubeListBuilder.create(), PartPose.ZERO);
        return meshdefinition;
    }

    public void setupAnim(S p_364685_) {
        super.setupAnim(p_364685_);
        float f = p_364685_.attackTime;
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, p_364685_.isAggressive, f, p_364685_.ageInTicks);
    }

    public void translateToArms(ZombieVillagerRenderState p_426866_, PoseStack p_425774_) {
        this.translateToHand(p_426866_, HumanoidArm.RIGHT, p_425774_);
    }
}