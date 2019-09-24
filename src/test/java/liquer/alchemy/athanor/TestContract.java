package liquer.alchemy.athanor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public class TestContract {
	
	private boolean primitiveBooleanValue;
	private Boolean booleanValue;
	private byte primitiveByteValue;
	private Byte byteValue;
	private short primitiveShortValue;
	private Short shortValue;
	private int intValue;
	private Integer integerValue;
	private long primitiveLongValue;
	private Long longValue;
	private float primitiveFloatValue;
	private Float floatValue;
	private double primitiveDoubleValue;
	private Double doubleValue;
	private BigDecimal bigDecimalValue;
	private BigInteger bigIntegerValue;
	private String stringValue;
	private Date dateValue;
	private int[] intArray;
	private List<Integer> integerList;
	private TestContract nestedContract;
	
	public TestContract() { 
	}


	public boolean isPrimitiveBooleanValue() {
		return primitiveBooleanValue;
	}


	public void setPrimitiveBooleanValue(boolean primitiveBooleanValue) {
		this.primitiveBooleanValue = primitiveBooleanValue;
	}


	public Boolean getBooleanValue() {
		return booleanValue;
	}


	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}


	public byte getPrimitiveByteValue() {
		return primitiveByteValue;
	}


	public void setPrimitiveByteValue(byte primitiveByteValue) {
		this.primitiveByteValue = primitiveByteValue;
	}


	public Byte getByteValue() {
		return byteValue;
	}


	public void setByteValue(Byte byteValue) {
		this.byteValue = byteValue;
	}


	public short getPrimitiveShortValue() {
		return primitiveShortValue;
	}


	public void setPrimitiveShortValue(short primitiveShortValue) {
		this.primitiveShortValue = primitiveShortValue;
	}


	public Short getShortValue() {
		return shortValue;
	}


	public void setShortValue(Short shortValue) {
		this.shortValue = shortValue;
	}


	public int getIntValue() {
		return intValue;
	}


	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}


	public Integer getIntegerValue() {
		return integerValue;
	}


	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}


	public long getPrimitiveLongValue() {
		return primitiveLongValue;
	}


	public void setPrimitiveLongValue(long primitiveLongValue) {
		this.primitiveLongValue = primitiveLongValue;
	}


	public Long getLongValue() {
		return longValue;
	}


	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}


	public float getPrimitiveFloatValue() {
		return primitiveFloatValue;
	}


	public void setPrimitiveFloatValue(float primitiveFloatValue) {
		this.primitiveFloatValue = primitiveFloatValue;
	}


	public Float getFloatValue() {
		return floatValue;
	}


	public void setFloatValue(Float floatValue) {
		this.floatValue = floatValue;
	}


	public double getPrimitiveDoubleValue() {
		return primitiveDoubleValue;
	}


	public void setPrimitiveDoubleValue(double primitiveDoubleValue) {
		this.primitiveDoubleValue = primitiveDoubleValue;
	}


	public Double getDoubleValue() {
		return doubleValue;
	}


	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}


	public BigDecimal getBigDecimalValue() {
		return bigDecimalValue;
	}


	public void setBigDecimalValue(BigDecimal bigDecimalValue) {
		this.bigDecimalValue = bigDecimalValue;
	}


	public BigInteger getBigIntegerValue() {
		return bigIntegerValue;
	}


	public void setBigIntegerValue(BigInteger bigIntegerValue) {
		this.bigIntegerValue = bigIntegerValue;
	}


	public String getStringValue() {
		return stringValue;
	}


	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}


	public Date getDateValue() {
		return dateValue;
	}


	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}
	
	public TestContract getNestedContract() {
		return nestedContract;
	}
	
	public void setNestedContract(TestContract test) {
		nestedContract = test;
	}

	public int[] getIntArray() {
		return intArray;
	}

	public void setIntArray(int[] intArray) {
		this.intArray = intArray;
	}
	
	public List<Integer> getIntegerList() {
		return integerList;
	}

	public void setIntegerList(List<Integer> integerList) {
		this.integerList = integerList;
	}
}
