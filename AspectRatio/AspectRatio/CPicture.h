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
	BITMAPINFO m_bmpGauss;
	RGBQUAD* m_frame;
	CRect m_rect;
	RGBQUAD* m_stretchFrame;
	RGBQUAD* m_gaussianFrame;
	CComPtr<IStretchImage> m_pScaler;
	bool m_init = false;
	void CreateGaussianFrame();
	RGBQUAD* CreateSideImage(int width, int height, int originalWidth, boolean left);
	RGBQUAD* CreateSubImage(RGBQUAD* original, int x, int y, int width, int height);
	RGBQUAD* ProcessImage(RGBQUAD* image, int width, int height);
	void BuildGaussImage(int width, int height, RGBQUAD* originalImage, int leftWidth, RGBQUAD* leftSide, int rightWidth, RGBQUAD* rightSide);
	RGBQUAD* Zoom(RGBQUAD* image, int width, int height, int times);
	RGBQUAD* Gauss(RGBQUAD* image, int width, int height, int radius);
	int* boxesForGauss(float sigma, int n);
	void boxBlur_4(int* source, int* dest, int width, int height, int radius);
	void boxBlurH_4(int* source, int* dest, int width, int height, int radius);
	void boxBlurT_4(int* source, int* dest, int width, int height, int radius);
	void gaussBlur_4(int* source, int* dest, int width, int height, int radius);
};

