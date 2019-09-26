package liquer.alchemy.crypto.sig.v4;

import liquer.alchemy.support.StringSupport;

import java.text.ParseException;
import java.util.Date;

public class CredentialScope {

	public static final String TERMINATION = "aws4_request";

	private Date date;
	private String regionName;
	private String serviceName;
	private String terminationString;

	public static CredentialScope parse(String value) throws ParseException {
		String[] parts = value.split("/", 4);
		if (parts.length == 4) {
			return new CredentialScope(SignV4Utils.parseS4DateStamp(parts[0]), parts[1], parts[2], parts[3]);
		}
		throw new ParseException(value, 0);
	}
	public CredentialScope(Date date, String regionName, String serviceName, String terminationString) {
		this.date = new Date(date.getTime());
		this.regionName = regionName;
		this.serviceName = serviceName;
		this.terminationString = StringSupport.isNullEmptyOrBlank(terminationString) ? TERMINATION : terminationString;
	}
	public Date getDate() {
		return new Date(date.getTime());
	}
	public void setDate(Date date) {
		this.date = date == null ? null : new Date(date.getTime());
	}
	public String getRegionName() {
		return regionName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public String getTerminationString() {
		return terminationString;
	}
	@Override public String toString() {
		return String.format("%1$s/%2$s/%3$s/%4$s", SignV4Utils.getS4DateStamp(date), regionName, serviceName, terminationString);
	}
}
