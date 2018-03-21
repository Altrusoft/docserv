package se.altrusoft.docserv.models.immutable;

public class ImageSpecification {


	public final Double width;
	public final Double height;
	public final String url;

	public ImageSpecification() {
		width = null;
		height = null;
		url = null;
	}


	public ImageSpecification(final Double width, final Double height, final String url) {
		this.width = width;
		this.height = height;
		this.url = url;
	}

}
