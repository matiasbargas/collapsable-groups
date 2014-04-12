package collapsable.widget;

/*
 * Example taked from Eclipse snippets
 * 
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CollapsableGroupExample {

	public static void main(String[] args) {

		Display display = new Display();
		final Image image = new Image(display, 20, 20);
		Color color = display.getSystemColor(SWT.COLOR_RED);
		GC gc = new GC(image);
		gc.setBackground(color);
		gc.fillRectangle(image.getBounds());
		gc.dispose();

		Shell shell = new Shell(display);
		shell.setBounds(SWT.DEFAULT, SWT.DEFAULT, 40, 40);
		GridLayoutFactory.fillDefaults().applyTo(shell);
		GridDataFactory.fillDefaults().applyTo(shell);

		Label someTextBeforeGroup = new Label(shell, SWT.BOLD);
		someTextBeforeGroup.setText("this is a text before collapsableGroup");

		CollapsableGroup group = new CollapsableGroup(shell, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(group);
		GridDataFactory.fillDefaults().applyTo(group);

		group.setText("a square");
		Canvas canvas = new Canvas(group.getContent(), SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image, 0, 0);
			}
		});

		Label someTextAfterGroup = new Label(shell, SWT.BOLD);
		someTextAfterGroup.setText("this is a text after collapsableGroup");

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		image.dispose();
		display.dispose();
	}
}