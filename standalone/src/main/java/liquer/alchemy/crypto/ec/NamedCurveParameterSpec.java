package liquer.alchemy.crypto.ec;

import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;

public class NamedCurveParameterSpec extends ECParameterSpec {

	public static NamedCurveParameterSpec getInstance(ECNamedCurve namedCurve) throws InvalidParameterSpecException, NoSuchProviderException, NoSuchAlgorithmException {
		AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", "SunEC");
		parameters.init(new ECGenParameterSpec(namedCurve.name()));
		ECParameterSpec spec =  parameters.getParameterSpec(ECParameterSpec.class);
		return new NamedCurveParameterSpec(spec, namedCurve);
	}


	private final ECNamedCurve namedCurve;

	private NamedCurveParameterSpec(ECParameterSpec spec, ECNamedCurve namedCurve ) {
		super(spec.getCurve(), spec.getGenerator(), spec.getOrder(), spec.getCofactor());
		this.namedCurve = namedCurve;
	}

	public ECNamedCurve getNamedCurve() {
		return namedCurve;
	}

}
