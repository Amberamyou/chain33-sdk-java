package cn.chain33.javasdk.model.abi.datatypes.generated;

import cn.chain33.javasdk.model.abi.datatypes.Int;

import java.math.BigInteger;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use cn.chain33.javasdk.model.codegen.AbiTypesGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Int184 extends Int {
    public static final Int184 DEFAULT = new Int184(BigInteger.ZERO);

    public Int184(BigInteger value) {
        super(184, value);
    }

    public Int184(long value) {
        this(BigInteger.valueOf(value));
    }
}
