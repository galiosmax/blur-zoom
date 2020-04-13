#include "pch.h"
#include "CPicture.h"
#include <ctime>
#include <iostream>
#include <cmath>

const double CPicture::m_proportion = 16.0 / 9.0;

CPicture::~CPicture()
{
	delete[] m_stretchFrame;

}

void CPicture::Init(BITMAPINFO bmp_new)
{
	m_bmp = bmp_new;
	this->GetWindowRect(&m_rect);

	m_bmpOut = bmp_new;
	memcpy(&m_bmp, &bmp_new, sizeof(BITMAPINFO));
	int height = m_bmp.bmiHeader.biHeight;

	m_bmp.bmiHeader.biWidth = m_rect.Width();
	m_bmp.bmiHeader.biHeight = m_rect.Height();
	int size = m_rect.Width() * m_rect.Height();
	m_bmp.bmiHeader.biSizeImage = 4 * size;
	m_stretchFrame = new RGBQUAD[size];

	int width = (int)floor(height * m_proportion);

	m_gaussianFrame = new RGBQUAD[height * width];

	ATLASSERT(m_pScaler.CoCreateInstance(ICONVERTIMAGELib::CLSID_StretchImage) == S_OK);
	m_init = true;
}

BEGIN_MESSAGE_MAP(CPicture, CWnd)
	ON_WM_PAINT()
END_MESSAGE_MAP()


void CPicture::OnPaint()
{
	CPaintDC dc(this); // device context for painting

	if (!m_init)
	{
		return;
	}

	CreateGaussianFrame();
	/*HRESULT hr = m_pScaler->StretchImage(reinterpret_cast<unsigned char*>(m_stretchFrame), reinterpret_cast<unsigned char*>(gaussianFrame),
		m_rect.Width(), m_rect.Height(), m_bmpOut.bmiHeader.biWidth, m_bmpOut.bmiHeader.biHeight,
		m_rect.Width()* m_bmp.bmiHeader.biBitCount / 8, m_bmpOut.bmiHeader.biWidth* m_bmpOut.bmiHeader.biBitCount / 8, 8);

	ATLASSERT(hr == S_OK);*/
	SetDIBitsToDevice(dc, 0, 0, m_rect.Width(), m_rect.Height(), 0, 0, 0, m_rect.Height(), m_stretchFrame, &m_bmp, DIB_RGB_COLORS);;
}

void CPicture::Update(RGBQUAD* frame)
{
	m_frame = frame;
	Invalidate();
}

void CPicture::CreateGaussianFrame()
{
	unsigned int start_time = clock();

	int height = m_bmp.bmiHeader.biHeight;
	int width = (int) floor(height * m_proportion);

	RGBQUAD* toProcessLeft = CreateSideImage(width, true);
	RGBQUAD* toProcessRight = CreateSideImage(width, false);

	RGBQUAD* processedLeft = ProcessImage(toProcessLeft);
	RGBQUAD* processedRight = ProcessImage(toProcessRight);

	m_gaussianFrame = BuildGaussImage(m_frame, processedLeft, processedRight);

	delete[] toProcessLeft;
	delete[] toProcessRight;
	delete[] processedLeft;
	delete[] processedRight;

	printf("Gausszoom took %d ms", clock() - start_time);
}

RGBQUAD* CPicture::CreateSideImage(int width, boolean left)
{
	int subImageWidth = (width - m_bmp.bmiHeader.biWidth) / 4;
	int subImageHeight = m_bmp.bmiHeader.biHeight / 2;

	if (left)
	{
		return CreateSubImage(0, subImageHeight / 2, subImageWidth, subImageHeight, m_frame);
	}
	else
	{
		return CreateSubImage(m_bmp.bmiHeader.biWidth - subImageWidth - 1, subImageHeight / 2, subImageWidth, subImageHeight, m_frame);
	}
}

RGBQUAD* CPicture::CreateSubImage(int x, int y, int width, int height, RGBQUAD* original)
{
	RGBQUAD* subImage = new RGBQUAD[width * height];

	for (int j = 0; j < height; ++j)
	{
		memcpy(*(&subImage + j * width * sizeof(RGBQUAD)), *(&original + (x + (y + j) * m_bmp.bmiHeader.biWidth) * sizeof(RGBQUAD)), sizeof(RGBQUAD) * width);
	}

	return subImage;
}

RGBQUAD* CPicture::ProcessImage(RGBQUAD* image)
{
	return Gauss(Zoom(image, 2), 3);
}

RGBQUAD* CPicture::BuildGaussImage(RGBQUAD* originalImage, RGBQUAD* leftSide, RGBQUAD* rightSide)
{
	/*finalImage.setRGB(0, 0, processedLeft.getWidth(), processedLeft.getHeight(), processedLeft.getRGB(0, 0, processedLeft.getWidth(), processedLeft.getHeight(), null, 0, processedLeft.getWidth()), 0, processedLeft.getWidth());
	finalImage.setRGB(processedRight.getWidth() + original.getWidth() - 1, 0, processedRight.getWidth(), processedRight.getHeight(), processedRight.getRGB(0, 0, processedRight.getWidth(), processedRight.getHeight(), null, 0, processedRight.getWidth()), 0, processedRight.getWidth());
	finalImage.setRGB(width - original.getWidth() - processedLeft.getWidth() - 1, 0, original.getWidth(), height, original.getRGB(0, 0, original.getWidth(), original.getHeight(), null, 0, original.getWidth()), 0, original.getWidth());*/
	
	RGBQUAD
	
	return nullptr;
}

RGBQUAD* CPicture::Zoom(RGBQUAD* image, int times)
{
	return nullptr;
}

RGBQUAD* CPicture::Gauss(RGBQUAD* image, int radius)
{
	return nullptr;
}
 

