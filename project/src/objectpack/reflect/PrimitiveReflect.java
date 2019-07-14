package objectpack.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * プリミティブ値書き換えbyリフレクション. Byte, Double, Float, Integer, Long, Short.
 * これらの値をリフレクションで内部情報を直接変更.
 * 
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
public final class PrimitiveReflect {
    private PrimitiveReflect() {
    }

    protected static final String OVERRIDE_FIELD = "override";
    protected static final String ACQUIRE_FIELD_ACCESSOR = "acquireFieldAccessor";

    private static final Field bp;
    private static final Field bd;
    private static final Field bf;
    private static final Field bi;
    private static final Field bl;
    private static final Field bs;

    private static final sun.reflect.FieldAccessor sbp;
    private static final sun.reflect.FieldAccessor sbd;
    private static final sun.reflect.FieldAccessor sbf;
    private static final sun.reflect.FieldAccessor sbi;
    private static final sun.reflect.FieldAccessor sbl;
    private static final sun.reflect.FieldAccessor sbs;

    static {
        // SUNパッケージに対応している場合は、高速なリフレクションを利用.
        if (FastClassElement._IS_SUN_PACKAGE) {
            Field e = null;
            sun.reflect.FieldAccessor ee = null;
            Field mf;
            Boolean ov;
            Method am;

            try {
                e = java.lang.Byte.class.getDeclaredField("value");
                e.setAccessible(true);
                mf = e.getClass().getSuperclass()
                        .getDeclaredField(OVERRIDE_FIELD);
                mf.setAccessible(true);
                ov = (Boolean) mf.get(e);
                mf = null;
                am = e.getClass().getDeclaredMethod(ACQUIRE_FIELD_ACCESSOR,
                        boolean.class);
                am.setAccessible(true);
                ee = (sun.reflect.FieldAccessor) am.invoke(e, ov);
            } catch (Throwable t) {
                ee = null;
            }
            sbp = ee;

            try {
                e = java.lang.Double.class.getDeclaredField("value");
                e.setAccessible(true);
                mf = e.getClass().getSuperclass()
                        .getDeclaredField(OVERRIDE_FIELD);
                mf.setAccessible(true);
                ov = (Boolean) mf.get(e);
                mf = null;
                am = e.getClass().getDeclaredMethod(ACQUIRE_FIELD_ACCESSOR,
                        boolean.class);
                am.setAccessible(true);
                ee = (sun.reflect.FieldAccessor) am.invoke(e, ov);
            } catch (Throwable t) {
                ee = null;
            }
            sbd = ee;

            try {
                e = java.lang.Float.class.getDeclaredField("value");
                e.setAccessible(true);
                mf = e.getClass().getSuperclass()
                        .getDeclaredField(OVERRIDE_FIELD);
                mf.setAccessible(true);
                ov = (Boolean) mf.get(e);
                mf = null;
                am = e.getClass().getDeclaredMethod(ACQUIRE_FIELD_ACCESSOR,
                        boolean.class);
                am.setAccessible(true);
                ee = (sun.reflect.FieldAccessor) am.invoke(e, ov);
            } catch (Throwable t) {
                ee = null;
            }
            sbf = ee;

            try {
                e = java.lang.Integer.class.getDeclaredField("value");
                e.setAccessible(true);
                mf = e.getClass().getSuperclass()
                        .getDeclaredField(OVERRIDE_FIELD);
                mf.setAccessible(true);
                ov = (Boolean) mf.get(e);
                mf = null;
                am = e.getClass().getDeclaredMethod(ACQUIRE_FIELD_ACCESSOR,
                        boolean.class);
                am.setAccessible(true);
                ee = (sun.reflect.FieldAccessor) am.invoke(e, ov);
            } catch (Throwable t) {
                ee = null;
            }
            sbi = ee;

            try {
                e = java.lang.Long.class.getDeclaredField("value");
                e.setAccessible(true);
                mf = e.getClass().getSuperclass()
                        .getDeclaredField(OVERRIDE_FIELD);
                mf.setAccessible(true);
                ov = (Boolean) mf.get(e);
                mf = null;
                am = e.getClass().getDeclaredMethod(ACQUIRE_FIELD_ACCESSOR,
                        boolean.class);
                am.setAccessible(true);
                ee = (sun.reflect.FieldAccessor) am.invoke(e, ov);
            } catch (Throwable t) {
                ee = null;
            }
            sbl = ee;

            try {
                e = java.lang.Short.class.getDeclaredField("value");
                e.setAccessible(true);
                mf = e.getClass().getSuperclass()
                        .getDeclaredField(OVERRIDE_FIELD);
                mf.setAccessible(true);
                ov = (Boolean) mf.get(e);
                mf = null;
                am = e.getClass().getDeclaredMethod(ACQUIRE_FIELD_ACCESSOR,
                        boolean.class);
                am.setAccessible(true);
                ee = (sun.reflect.FieldAccessor) am.invoke(e, ov);
            } catch (Throwable t) {
                ee = null;
            }
            sbs = ee;

            bp = null;
            bd = null;
            bf = null;
            bi = null;
            bl = null;
            bs = null;
        }
        // SUNパッケージに対応していない場合は、通常のリフレクションで処理.
        else {
            Field e = null;

            try {
                e = java.lang.Byte.class.getDeclaredField("value");
                e.setAccessible(true);
            } catch (Throwable t) {
                e = null;
            }
            bp = e;

            try {
                e = java.lang.Double.class.getDeclaredField("value");
                e.setAccessible(true);
            } catch (Throwable t) {
                e = null;
            }
            bd = e;

            try {
                e = java.lang.Float.class.getDeclaredField("value");
                e.setAccessible(true);
            } catch (Throwable t) {
                e = null;
            }
            bf = e;

            try {
                e = java.lang.Integer.class.getDeclaredField("value");
                e.setAccessible(true);
            } catch (Throwable t) {
                e = null;
            }
            bi = e;

            try {
                e = java.lang.Long.class.getDeclaredField("value");
                e.setAccessible(true);
            } catch (Throwable t) {
                e = null;
            }
            bl = e;

            try {
                e = java.lang.Short.class.getDeclaredField("value");
                e.setAccessible(true);
            } catch (Throwable t) {
                e = null;
            }
            bs = e;

            sbp = null;
            sbd = null;
            sbf = null;
            sbi = null;
            sbl = null;
            sbs = null;
        }
    }

    /**
     * Byteプリミティブを変更.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void changeByte(Byte p, Number v) throws Exception {
        if (bp == null) {
            sbp.set(p, v.byteValue());
        } else {
            bp.set(p, v.byteValue());
        }
    }

    /**
     * Byteプリミティブに１プラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addByte(Byte p) throws Exception {
        if (bp == null) {
            sbp.set(p, (byte) (p.intValue() + 1));
        } else {
            bp.set(p, (byte) (p.intValue() + 1));
        }
    }

    /**
     * Byteプリミティブに１マイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decByte(Byte p) throws Exception {
        if (bp == null) {
            sbp.set(p, (byte) (p.intValue() - 1));
        } else {
            bp.set(p, (byte) (p.intValue() - 1));
        }
    }

    /**
     * Byteプリミティブにプラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addByte(Byte p, Number v) throws Exception {
        if (bp == null) {
            sbp.set(p, (byte) (p.intValue() + v.intValue()));
        } else {
            bp.set(p, (byte) (p.intValue() + v.intValue()));
        }
    }

    /**
     * Byteプリミティブにマイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decByte(Byte p, Number v) throws Exception {
        if (bp == null) {
            sbp.set(p, (byte) (p.intValue() - v.intValue()));
        } else {
            bp.set(p, (byte) (p.intValue() - v.intValue()));
        }
    }

    /**
     * Byteプリミティブに掛け算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void mulByte(Byte p, Number v) throws Exception {
        if (bp == null) {
            sbp.set(p, (byte) (p.intValue() * v.intValue()));
        } else {
            bp.set(p, (byte) (p.intValue() * v.intValue()));
        }
    }

    /**
     * Byteプリミティブに割り算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void divByte(Byte p, Number v) throws Exception {
        if (bp == null) {
            sbp.set(p, (byte) (p.intValue() / v.intValue()));
        } else {
            bp.set(p, (byte) (p.intValue() / v.intValue()));
        }
    }

    /**
     * Shortプリミティブを変更.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void changeShort(Short p, Number v) throws Exception {
        if (bs == null) {
            sbs.set(p, v.shortValue());
        } else {
            bs.set(p, v.shortValue());
        }
    }

    /**
     * Shortプリミティブに１プラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addShort(Short p) throws Exception {
        if (bs == null) {
            sbs.set(p, (short) (p.intValue() + 1));
        } else {
            bs.set(p, (short) (p.intValue() + 1));
        }
    }

    /**
     * Shortプリミティブに１マイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decShort(Short p) throws Exception {
        if (bs == null) {
            sbs.set(p, (short) (p.intValue() - 1));
        } else {
            bs.set(p, (short) (p.intValue() - 1));
        }
    }

    /**
     * Shortプリミティブにプラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addShort(Short p, Number v) throws Exception {
        if (bs == null) {
            sbs.set(p, (short) (p.intValue() + v.intValue()));
        } else {
            bs.set(p, (short) (p.intValue() + v.intValue()));
        }
    }

    /**
     * Shortプリミティブにマイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decShort(Short p, Number v) throws Exception {
        if (bs == null) {
            sbs.set(p, (short) (p.intValue() - v.intValue()));
        } else {
            bs.set(p, (short) (p.intValue() - v.intValue()));
        }
    }

    /**
     * Shortプリミティブに掛け算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void mulShort(Short p, Number v) throws Exception {
        if (bs == null) {
            sbs.set(p, (short) (p.intValue() * v.intValue()));
        } else {
            bs.set(p, (short) (p.intValue() * v.intValue()));
        }
    }

    /**
     * Shortプリミティブに割り算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void divShort(Short p, Number v) throws Exception {
        if (bs == null) {
            sbs.set(p, (short) (p.intValue() / v.intValue()));
        } else {
            bs.set(p, (short) (p.intValue() / v.intValue()));
        }
    }

    /**
     * Integerプリミティブを変更.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void changeInteger(Integer p, Number v)
            throws Exception {
        if (bi == null) {
            sbi.set(p, v.intValue());
        } else {
            bi.set(p, v.intValue());
        }
    }

    /**
     * Integerプリミティブに１プラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addInteger(Integer p) throws Exception {
        if (bi == null) {
            sbi.set(p, p.intValue() + 1);
        } else {
            bi.set(p, p.intValue() + 1);
        }
    }

    /**
     * Integerプリミティブに１マイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decInteger(Integer p) throws Exception {
        if (bi == null) {
            sbi.set(p, p.intValue() - 1);
        } else {
            bi.set(p, p.intValue() - 1);
        }
    }

    /**
     * Integerプリミティブにプラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addInteger(Integer p, Number v) throws Exception {
        if (bi == null) {
            sbi.set(p, p.intValue() + v.intValue());
        } else {
            bi.set(p, p.intValue() + v.intValue());
        }
    }

    /**
     * Integerプリミティブにマイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decInteger(Integer p, Number v) throws Exception {
        if (bi == null) {
            sbi.set(p, p.intValue() - v.intValue());
        } else {
            bi.set(p, p.intValue() - v.intValue());
        }
    }

    /**
     * Integerプリミティブに掛け算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void mulInteger(Integer p, Number v) throws Exception {
        if (bi == null) {
            sbi.set(p, p.intValue() * v.intValue());
        } else {
            bi.set(p, p.intValue() * v.intValue());
        }
    }

    /**
     * Integerプリミティブに割り算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void divInteger(Integer p, Number v) throws Exception {
        if (bi == null) {
            sbi.set(p, p.intValue() / v.intValue());
        } else {
            bi.set(p, p.intValue() / v.intValue());
        }
    }

    /**
     * Longプリミティブを変更.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void changeLong(Long p, Number v) throws Exception {
        if (bl == null) {
            sbl.set(p, v.longValue());
        } else {
            bl.set(p, v.longValue());
        }
    }

    /**
     * Longプリミティブに１プラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addLong(Long p) throws Exception {
        if (bl == null) {
            sbl.set(p, p.longValue() + 1L);
        } else {
            bl.set(p, p.longValue() + 1L);
        }
    }

    /**
     * Longプリミティブに１マイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decLong(Long p) throws Exception {
        if (bl == null) {
            sbl.set(p, p.longValue() - 1L);
        } else {
            bl.set(p, p.longValue() - 1L);
        }
    }

    /**
     * Longプリミティブにプラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addLong(Long p, Number v) throws Exception {
        if (bl == null) {
            sbl.set(p, p.longValue() + v.longValue());
        } else {
            bl.set(p, p.longValue() + v.longValue());
        }
    }

    /**
     * Longプリミティブにマイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decLong(Long p, Number v) throws Exception {
        if (bl == null) {
            sbl.set(p, p.longValue() - v.longValue());
        } else {
            bl.set(p, p.longValue() - v.longValue());
        }
    }

    /**
     * Longプリミティブに掛け算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void mulLong(Long p, Number v) throws Exception {
        if (bl == null) {
            sbl.set(p, p.longValue() * v.longValue());
        } else {
            bl.set(p, p.longValue() * v.longValue());
        }
    }

    /**
     * Longプリミティブに割り算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void divLong(Long p, Number v) throws Exception {
        if (bl == null) {
            sbl.set(p, p.longValue() / v.longValue());
        } else {
            bl.set(p, p.longValue() / v.longValue());
        }
    }

    /**
     * Floatプリミティブを変更.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void changeFloat(Float p, Number v) throws Exception {
        if (bf == null) {
            sbp.set(p, v.floatValue());
        } else {
            bp.set(p, v.floatValue());
        }
    }

    /**
     * Floatプリミティブに１プラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addFloat(Float p) throws Exception {
        if (bf == null) {
            sbf.set(p, p.floatValue() + 1F);
        } else {
            bf.set(p, p.floatValue() + 1F);
        }
    }

    /**
     * Floatプリミティブに１マイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decFloat(Float p) throws Exception {
        if (bf == null) {
            sbf.set(p, p.floatValue() - 1F);
        } else {
            bf.set(p, p.floatValue() - 1F);
        }
    }

    /**
     * Floatプリミティブにプラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addFloat(Float p, Number v) throws Exception {
        if (bf == null) {
            sbf.set(p, p.floatValue() + v.floatValue());
        } else {
            bf.set(p, p.floatValue() + v.floatValue());
        }
    }

    /**
     * Floatプリミティブにマイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decFloat(Float p, Number v) throws Exception {
        if (bf == null) {
            sbf.set(p, p.floatValue() - v.floatValue());
        } else {
            bf.set(p, p.floatValue() - v.floatValue());
        }
    }

    /**
     * Floatプリミティブに掛け算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void mulFloat(Float p, Number v) throws Exception {
        if (bf == null) {
            sbf.set(p, p.floatValue() * v.floatValue());
        } else {
            bf.set(p, p.floatValue() * v.floatValue());
        }
    }

    /**
     * Floatプリミティブに割り算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void divFloat(Float p, Number v) throws Exception {
        if (bf == null) {
            sbf.set(p, p.floatValue() / v.floatValue());
        } else {
            bf.set(p, p.floatValue() / v.floatValue());
        }
    }

    /**
     * Doubleプリミティブを変更.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void changeDouble(Double p, Number v) throws Exception {
        if (bd == null) {
            sbd.set(p, v.doubleValue());
        } else {
            bd.set(p, v.doubleValue());
        }
    }

    /**
     * Doubleプリミティブに１プラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addDouble(Double p) throws Exception {
        if (bd == null) {
            sbd.set(p, p.doubleValue() + 1D);
        } else {
            bd.set(p, p.doubleValue() + 1D);
        }
    }

    /**
     * Doubleプリミティブに１マイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decDouble(Double p) throws Exception {
        if (bd == null) {
            sbd.set(p, p.doubleValue() - 1D);
        } else {
            bd.set(p, p.doubleValue() - 1D);
        }
    }

    /**
     * Doubleプリミティブにプラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void addDouble(Double p, Number v) throws Exception {
        if (bd == null) {
            sbd.set(p, p.doubleValue() + v.doubleValue());
        } else {
            bd.set(p, p.doubleValue() + v.doubleValue());
        }
    }

    /**
     * Doubleプリミティブにマイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void decDouble(Double p, Number v) throws Exception {
        if (bd == null) {
            sbd.set(p, p.doubleValue() - v.doubleValue());
        } else {
            bd.set(p, p.doubleValue() - v.doubleValue());
        }
    }

    /**
     * Doubleプリミティブに掛け算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void mulDouble(Double p, Number v) throws Exception {
        if (bd == null) {
            sbd.set(p, p.doubleValue() * v.doubleValue());
        } else {
            bd.set(p, p.doubleValue() * v.doubleValue());
        }
    }

    /**
     * Doubleプリミティブに割り算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void divDouble(Double p, Number v) throws Exception {
        if (bd == null) {
            sbd.set(p, p.doubleValue() / v.doubleValue());
        } else {
            bd.set(p, p.doubleValue() / v.doubleValue());
        }
    }

    /**
     * 対応プリミティブチェック.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @return boolean [true]の場合は、対応しています.
     */
    public static final boolean isPrimitive(Number p) {
        if (p instanceof Byte || p instanceof Short || p instanceof Integer
                || p instanceof Long || p instanceof Float
                || p instanceof Double) {
            return true;
        }
        return false;
    }

    /**
     * プリミティブを変更.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void change(Number p, Number v) throws Exception {
        if (p instanceof Integer) {
            changeInteger((Integer) p, v);
        } else if (p instanceof Long) {
            changeLong((Long) p, v);
        } else if (p instanceof Float) {
            changeFloat((Float) p, v);
        } else if (p instanceof Double) {
            changeDouble((Double) p, v);
        } else if (p instanceof Byte) {
            changeByte((Byte) p, v);
        } else if (p instanceof Short) {
            changeShort((Short) p, v);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * プリミティブに１プラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void add(Number p) throws Exception {
        if (p instanceof Integer) {
            addInteger((Integer) p);
        } else if (p instanceof Long) {
            addLong((Long) p);
        } else if (p instanceof Float) {
            addFloat((Float) p);
        } else if (p instanceof Double) {
            addDouble((Double) p);
        } else if (p instanceof Byte) {
            addByte((Byte) p);
        } else if (p instanceof Short) {
            addShort((Short) p);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * プリミティブに１マイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public static final void dec(Number p) throws Exception {
        if (p instanceof Integer) {
            decInteger((Integer) p);
        } else if (p instanceof Long) {
            decLong((Long) p);
        } else if (p instanceof Float) {
            decFloat((Float) p);
        } else if (p instanceof Double) {
            decDouble((Double) p);
        } else if (p instanceof Byte) {
            decByte((Byte) p);
        } else if (p instanceof Short) {
            decShort((Short) p);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * プリミティブにプラス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void add(Number p, Number v) throws Exception {
        if (p instanceof Integer) {
            addInteger((Integer) p, v);
        } else if (p instanceof Long) {
            addLong((Long) p, v);
        } else if (p instanceof Float) {
            addFloat((Float) p, v);
        } else if (p instanceof Double) {
            addDouble((Double) p, v);
        } else if (p instanceof Byte) {
            addByte((Byte) p, v);
        } else if (p instanceof Short) {
            addShort((Short) p, v);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * プリミティブにマイナス.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void dec(Number p, Number v) throws Exception {
        if (p instanceof Integer) {
            decInteger((Integer) p, v);
        } else if (p instanceof Long) {
            decLong((Long) p, v);
        } else if (p instanceof Float) {
            decFloat((Float) p, v);
        } else if (p instanceof Double) {
            decDouble((Double) p, v);
        } else if (p instanceof Byte) {
            decByte((Byte) p, v);
        } else if (p instanceof Short) {
            decShort((Short) p, v);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * プリミティブに掛け算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void mul(Number p, Number v) throws Exception {
        if (p instanceof Integer) {
            mulInteger((Integer) p, v);
        } else if (p instanceof Long) {
            mulLong((Long) p, v);
        } else if (p instanceof Float) {
            mulFloat((Float) p, v);
        } else if (p instanceof Double) {
            mulDouble((Double) p, v);
        } else if (p instanceof Byte) {
            mulByte((Byte) p, v);
        } else if (p instanceof Short) {
            mulShort((Short) p, v);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * プリミティブに割り算.
     * 
     * @param p
     *            対象のプリミティブオブジェクトを設定します.
     * @param v
     *            変更対象の値を設定します.
     * @exception Exception
     *                例外.
     */
    public static final void div(Number p, Number v) throws Exception {
        if (p instanceof Integer) {
            divInteger((Integer) p, v);
        } else if (p instanceof Long) {
            divLong((Long) p, v);
        } else if (p instanceof Float) {
            divFloat((Float) p, v);
        } else if (p instanceof Double) {
            divDouble((Double) p, v);
        } else if (p instanceof Byte) {
            divByte((Byte) p, v);
        } else if (p instanceof Short) {
            divShort((Short) p, v);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
