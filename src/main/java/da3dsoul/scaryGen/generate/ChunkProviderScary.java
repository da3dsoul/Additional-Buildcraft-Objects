package da3dsoul.scaryGen.generate;

import abo.ABO;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;

public class ChunkProviderScary extends ChunkProviderGenDummy {

	private byte	index;

	public ChunkProviderScary(World par1World, long par2, boolean par4, byte i) {
		super(par1World, par2, par4);
		index = i;
	}

	@Override
	public void generateTerrain(int par1, int par2, Block[] blockArray) {
		byte b0 = (byte) ABO.scaryGenWaterLevel;
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(
				this.biomesForGeneration, par1 * 4 - 2, par2 * 4 - 2, 10, 10);
		this.func_147423_a(par1 * 4, 0, par2 * 4);

		for (int k = 0; k < 4; ++k) {
			int l = k * 5;
			int i1 = (k + 1) * 5;

			for (int j1 = 0; j1 < 4; ++j1) {
				int k1 = (l + j1) * 33;
				int l1 = (l + j1 + 1) * 33;
				int i2 = (i1 + j1) * 33;
				int j2 = (i1 + j1 + 1) * 33;

				for (int k2 = 0; k2 < 32; ++k2) {
					double d0 = 0.125D;
					double d1 = this.field_147434_q[k1 + k2];
					double d2 = this.field_147434_q[l1 + k2];
					double d3 = this.field_147434_q[i2 + k2];
					double d4 = this.field_147434_q[j2 + k2];
					double d5 = (this.field_147434_q[k1 + k2 + 1] - d1) * d0;
					double d6 = (this.field_147434_q[l1 + k2 + 1] - d2) * d0;
					double d7 = (this.field_147434_q[i2 + k2 + 1] - d3) * d0;
					double d8 = (this.field_147434_q[j2 + k2 + 1] - d4) * d0;

					for (int l2 = 0; l2 < 8; ++l2) {
						double d9 = 0.35D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for (int i3 = 0; i3 < 4; ++i3) {
							int j3 = ((i3 + k * 4) << 12) | ((0 + j1 * 4) << 8) | (k2 * 8 + l2);
							short short1 = 256;
							j3 -= short1;
							double d14 = 0.25D;
							double d16 = (d11 - d10) * d14;
							double d15 = d10 - d16;

							for (int k3 = 0; k3 < 4; ++k3) {
								if ((d15 += d16) > 0.0D) {
									if(k2 * 8 + l2 < ABO.scaryGenHeightLimit)
									{
										blockArray[j3 += short1] = Blocks.stone;
									} else {
										blockArray[j3 += short1] = null;
									}
								} else if (k2 * 8 + l2 < b0) {
									blockArray[j3 += short1] = ABO.scaryGenWaterLevelReplacement;
								} else {
									blockArray[j3 += short1] = null;
								}
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unused")
	protected void func_147423_a(int par1, int par2, int par3) {
		byte sc = index;

		double d0 = 684.412D;
		double d1 = 684.412D;
		double d2 = 512.0D;
		double d3 = 512.0D;

		switch (sc) {
			case 1:
				setupNoiseGens1(par1, par2, par3);
				break;
			case 4:
				setupNoiseGens4(par1, par2, par3);
				break;
			default:
				setupNoiseGensDefault(par1, par2, par3);
				break;
		}

		boolean flag1 = false;
		boolean flag = false;
		int l = 0;
		int i1 = 0;
		double d4 = 8.5D;

		for (int j1 = 0; j1 < 5; ++j1) {
			for (int k1 = 0; k1 < 5; ++k1) {
				float f = 0.0F;
				float f1 = 0.0F;
				float f2 = 0.0F;
				byte b0 = 2;
				BiomeGenBase biomegenbase = this.biomesForGeneration[j1 + 2 + (k1 + 2) * 10];

				for (int l1 = -b0; l1 <= b0; ++l1) {
					for (int i2 = -b0; i2 <= b0; ++i2) {
						BiomeGenBase biomegenbase1 = this.biomesForGeneration[j1 + l1 + 2 + (k1 + i2 + 2) * 10];
						float f3 = biomegenbase1.rootHeight;
						float f4 = biomegenbase1.heightVariation;

						if (this.field_147435_p == WorldType.AMPLIFIED && f3 > 0.0F) {
							f3 = 1.0F + f3 * 2.0F;
							f4 = 1.0F + f4 * 4.0F;
						}

						float f5 = this.parabolicField[l1 + 2 + (i2 + 2) * 5] / (f3 + 2.0F);

						if (biomegenbase1.rootHeight > biomegenbase.rootHeight) {
							f5 /= 2.0F;
						}

						f += f4 * f5;
						f1 += f3 * f5;
						f2 += f5;
					}
				}

				f /= f2;
				f1 /= f2;
				f = f * 0.9F + 0.1F;
				f1 = (f1 * 4.0F - 1.0F) / 8.0F;
				double d12 = this.noiseGenDoubleArray1[i1] / 8000.0D;

				if (d12 < 0.0D) {
					d12 = -d12 * 0.3D;
				}

				d12 = d12 * 3.0D - 2.0D;

				if (d12 < 0.0D) {
					d12 /= 2.0D;

					if (d12 < -1.0D) {
						d12 = -1.0D;
					}

					d12 /= 1.4D;
					d12 /= 2.0D;
				} else {
					if (d12 > 1.0D) {
						d12 = 1.0D;
					}

					d12 /= 8.0D;
				}

				++i1;
				double d13 = (double) f1;
				double d14 = (double) f;
				d13 += d12 * 0.2D;
				d13 = d13 * 8.5D / 8.0D;
				double d5 = 8.5D + d13 * 4.0D;

				for (int j2 = 0; j2 < 33; ++j2) {
					double d6 = ((double) j2 - d5) * 12.0D * 128.0D / 256.0D / d14;

					if (d6 < 0.0D) {
						d6 *= 4.0D;
					}

					double d7 = this.noiseGenDoubleArray3[l] / 512.0D;
					double d8 = this.noiseGenDoubleArray4[l] / 512.0D;
					double d9 = (this.noiseGenDoubleArray2[l] / 10.0D + 1.0D) / 2.0D;

					double a[] = null;

					switch (sc) {
						case 1:
							a = applyScaryNoise1(d7, d8);
							break;
						case 2:
							a = applyScaryNoise2(d7, d8);
							break;
						case 3:
							a = applyScaryNoise3(d7, d8);
							break;
						case 4:
							a = applyScaryNoise4(d7, d8);
							break;
						case 5:
							a = applyScaryNoise5(d7, d8);
							break;
						case 6:
							a = applyScaryNoise6(d7, d8);
							break;
						default:
							a = applyScaryNoise1(d7, d8);
							break;
					}

					d7 = a[0];
					d8 = a[1];

					double d10 = MathHelper.denormalizeClamp(d7, d8, d9) - d6;

					if (j2 > 29) {
						double d11 = (double) ((float) (j2 - 29) / 3.0F);
						d10 = d10 * (1.0D - d11) + -10.0D * d11;
					}

					this.field_147434_q[l] = d10;
					++l;
				}
			}
		}
	}

	private void setupNoiseGensDefault(int par1, int par2, int par3) {
		this.noiseGenDoubleArray1 = this.noiseGenOctaves6.generateNoiseOctaves(this.noiseGenDoubleArray1, par1, par3,
				5, 5, 200.0D, 200.0D, 0.5D);
		this.noiseGenDoubleArray2 = this.noiseGenOctaves3.generateNoiseOctaves(this.noiseGenDoubleArray2, par1, par2,
				par3, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
		this.noiseGenDoubleArray3 = this.noiseGenOctaves1.generateNoiseOctaves(this.noiseGenDoubleArray3, par1, par2,
				par3, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		this.noiseGenDoubleArray4 = this.noiseGenOctaves2.generateNoiseOctaves(this.noiseGenDoubleArray4, par1, par2,
				par3, 5, 33, 5, 684.412D, 684.412D, 684.412D);
	}

	private void setupNoiseGens1(int par1, int par2, int par3) {
		this.noiseGenDoubleArray1 = this.noiseGenOctaves6.generateNoiseOctaves(this.noiseGenDoubleArray1, par1, par3,
				5, 5, 260.0D, 120.0D, 0.8D);
		this.noiseGenDoubleArray2 = this.noiseGenOctaves3.generateNoiseOctaves(this.noiseGenDoubleArray2, par1, par2,
				par3, 5, 33, 5, 0.106939375D, 0.7310D, 0.0855515D);
		this.noiseGenDoubleArray3 = this.noiseGenOctaves1.generateNoiseOctaves(this.noiseGenDoubleArray3, par1, par2,
				par3, 5, 33, 5, 684.412D, 821.2944D, 701.5223D);
		this.noiseGenDoubleArray4 = this.noiseGenOctaves2.generateNoiseOctaves(this.noiseGenDoubleArray4, par1, par2,
				par3, 5, 33, 5, 684.412D, 804.412D, 764.412D);
	}

	private void setupNoiseGens4(int par1, int par2, int par3) {
		this.noiseGenDoubleArray1 = this.noiseGenOctaves6.generateNoiseOctaves(this.noiseGenDoubleArray1, par1, par3,
				5, 5, 240.0D, 240.0D, 1.5D);
		this.noiseGenDoubleArray2 = this.noiseGenOctaves3.generateNoiseOctaves(this.noiseGenDoubleArray2, par1, par2,
				par3, 5, 33, 5, 0.4277575D, 0.01671D, 0.4277575D);
		this.noiseGenDoubleArray3 = this.noiseGenOctaves1.generateNoiseOctaves(this.noiseGenDoubleArray3, par1, par2,
				par3, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		this.noiseGenDoubleArray4 = this.noiseGenOctaves2.generateNoiseOctaves(this.noiseGenDoubleArray4, par1, par2,
				par3, 5, 33, 5, 684.412D, 684.412D, 684.412D);
	}

	private double[] applyScaryNoise1(double d, double d1) {
		double[] a = new double[2];
		a[0] = 0.25 * exp(d, 3) * d1 + 0.125 * exp(d1, 3);
		a[1] = 0.25 * exp(d1, 2) + d * d1 + 0.125 * exp(d, 2);
		return a;
	}

	private double[] applyScaryNoise2(double d, double d1) {
		double[] a = new double[2];
		a[0] = d * d1 - exp(d, 2) + exp(d1, 2);
		a[1] = d * d1 + exp(d, 2) - exp(d1, 2);
		return a;
	}

	private double[] applyScaryNoise3(double d, double d1) {
		double[] a = new double[2];
		a[0] = ((d + d1) * (d - d1) * (d)) / (d1 * (d1 - d));
		a[1] = ((d + d1) * (d1 - d) * (d1)) / (d * (d - d1));
		return a;
	}

	private double[] applyScaryNoise4(double d, double d1) {
		double[] a = new double[2];
		a[0] = (exp(0.05 * d - 0.0125 * d1, 5)) * sign(d);
		a[1] = (exp(0.05 * d1 - 0.0125 * d, 5)) * sign(d1);
		return a;
	}

	private double[] applyScaryNoise5(double d, double d1) {
		double[] a = new double[2];
		a[0] = 0.25 * exp(d, 2) + d * d1 + 0.125 * exp(d1, 2);
		a[1] = 0.25 * exp(d1, 2) + d * d1 + 0.125 * exp(d, 2);
		return a;
	}

	private double[] applyScaryNoise6(double d, double d1) {
		double[] a = new double[2];
		a[0] = (exp(0.05 * d - 0.0125 * d1, 5)) * sign(d);
		a[1] = (exp(0.05 * d1 - 0.0125 * d, 5)) * sign(d1);
		return a;
	}

	private double exp(double d, int d1) {
		double a = d;
		do {
			if (d1 <= 0) break;
			a *= d;
			d1--;
		} while (true);
		return a;
	}

	private byte sign(double d) {
		return (byte) (d < 0 ? -1 : 1);
	}

}
