#pragma once
#include <afxwin.h>
class CPicture :
	public CWnd
{

public:
	~CPicture();
	void Init(BITMAPINFO bmp);
	void Update(RGBQUAD* frame);

	static const double m_proportion;

protected:
	DECLARE_MESSAGE_MAP()
	afx_msg void OnPaint();

	BITMAPINFO m_bmp;
	BITMAPINFO m_bmpOut;
	RGBQUAD* m_frame;
	CRect m_rect;
	RGBQUAD* m_stretchFrame;
	RGBQUAD* m_gaussianFrame;
	CComPtr<IStretchImage> m_pScaler;
	bool m_init = false;
	void CreateGaussianFrame();
	RGBQUAD* CreateSideImage(int width, boolean left);
	RGBQUAD* CreateSubImage(int x, int y, int width, int height, RGBQUAD* original);
	RGBQUAD* ProcessImage(RGBQUAD* image);
	RGBQUAD* BuildGaussImage(RGBQUAD* originalImage, RGBQUAD* leftSide, RGBQUAD* rightSide);
	RGBQUAD* Zoom(RGBQUAD* image, int times);
	RGBQUAD* Gauss(RGBQUAD* image, int radius);
};

