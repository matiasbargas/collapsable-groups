package collapsable.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;

public class CollapsableGroup extends Composite {

	private String id;
	private Button button;
	private Group group;
	private Composite content;

	private List<ICollapseListener> collapseListeners;

	private Listener resizeListener;
	private boolean isDefaultCollapsed;

	public CollapsableGroup(Composite parent, int style) {
		this(parent, style, false);
	}

	public CollapsableGroup(Composite parent, int style, boolean collapsed) {
		super(parent, SWT.FILL);
		id = UUID.randomUUID().toString();
		isDefaultCollapsed = collapsed;
		collapseListeners = new ArrayList<ICollapseListener>();
		createContents(style);
		listenForChanges();
	}

	private void createContents(int style) {
		super.setLayout(new CollapsableLayout());

		button = new Button(this, SWT.CHECK);
		expand();

		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				repaint();
				notifyListeners();
			}
		});

		group = new Group(this, style);
		GridLayoutFactory.fillDefaults().applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

		this.content = new Composite(group, SWT.NONE);

		if (isDefaultCollapsed) {
			collapse();
		}
	}

	public void collapse() {
		button.setSelection(false);
		notifyListeners();
	}

	public void expand() {
		button.setSelection(true);
		notifyListeners();
	}

	private void listenForChanges() {
		resizeListener = new Listener() {

			@Override
			public void handleEvent(final Event event) {
				repaint();
			}
		};

		if (getParent() != null) {
			getParent().addListener(SWT.Resize, resizeListener);
		}
		addListener(SWT.Resize, resizeListener);

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				Composite parent = CollapsableGroup.this.getParent();
				if (parent != null) {
					parent.removeListener(SWT.Resize, resizeListener);
					removeDisposeListener(this);
				}
			}
		});
	}

	@Override
	public Layout getLayout() {
		return this.content.getLayout();
	}

	@Override
	public void setLayout(final Layout layout) {
		this.content.setLayout(layout);
	}

	public String getText() {
		return this.button.getText();
	}

	public void setText(final String text) {
		this.button.setText(text);
	}

	@Override
	public Font getFont() {
		return this.button.getFont();
	}

	@Override
	public void setFont(final Font font) {
		this.button.setFont(font);
	}

	public Composite getContent() {
		return this.content;
	}

	@Override
	public boolean setFocus() {
		return this.content.setFocus();
	}

	/**
	 * @return <code>true</code> if the content is activated, <code>false</code>
	 *         otherwise
	 */
	public boolean isExpanded() {
		return this.button.getSelection();
	}

	/**
	 * @return <code>false</code> if the content is activated, <code>true</code>
	 *         otherwise
	 */
	public boolean isCollapsed() {
		return !this.button.getSelection();
	}

	public void addCollapseListener(ICollapseListener listener) {
		collapseListeners.add(listener);
	}

	private void repaint() {
		layout();
		if (getParent() != null) {
			getParent().layout();
		}
	}

	private void notifyListeners() {
		for (ICollapseListener listener : collapseListeners) {
			if (isExpanded()) {
				listener.onCollapse();
			} else {
				listener.onExpand();
			}
		}
	}

	/**
	 * @return an unique id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            an unique id to identify the group
	 */
	public void setId(String id) {
		this.id = id;
	}

	public class CollapsableLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint,
				boolean flushCache) {
			int parentWidth = composite.getParent().getBounds().width;

			Point buttonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			int height = buttonSize.y;

			if (isExpanded()) {
				Point groupSize = group.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				height += groupSize.y;
			}

			return new Point(parentWidth, height);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			Point buttonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			button.setBounds(15, 0, buttonSize.x, buttonSize.y);

			if (isExpanded()) {
				Point groupSize = group.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				group.setBounds(0, buttonSize.y, composite.getSize().x,
						groupSize.y);
			} else {
				group.setBounds(0, buttonSize.y, composite.getSize().x, 0);
			}
		}
	}

	public static interface ICollapseListener {

		public void onCollapse();

		public void onExpand();
	}
}
