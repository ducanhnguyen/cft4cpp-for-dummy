int pointerTest01(double* p1) {
    double a = *p1 + 1;
    if (a > 0)
        return 1;
    else
        return 0;
}